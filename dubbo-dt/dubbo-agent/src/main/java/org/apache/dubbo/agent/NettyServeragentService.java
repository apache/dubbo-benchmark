package org.apache.dubbo.agent;

import org.apache.dubbo.common.conf.TestMode;
import org.apache.dubbo.common.constant.Constant;
import org.apache.dubbo.common.entry.TestConfig;
import org.apache.dubbo.agent.handler.AgentNettyHandler;
import org.apache.dubbo.common.netty.NettyServer;
import io.netty.channel.ChannelHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

public class NettyServeragentService {

    private String safeGetEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }
    private Integer safeGetEnvInt(String key, Integer defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Long safeGetEnvLong(String key, Long defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Long safeGetDurationSeconds(String key, Long defaultValue) {
        return safeGetEnvLong(key, defaultValue);
    }
    private Thread nettyServerThread;
    private NettyServer nettyServer;

    @PostConstruct
    public void start() {
        nettyServerThread = new Thread(() -> {
            try {
                String spacename = safeGetEnv(Constant.SPRING_APPLICATION_NAME, Constant.SPRING_APPLICATION_NAME_DEFAULT);
                String servicePort = safeGetEnv(Constant.SERVICE_PORT, Constant.SERVICE_PORT_DEFAULT);
                Long durationSeconds = safeGetDurationSeconds(Constant.AGENT_DURATION_SECONDS, Constant.AGENT_DURATION_SECONDS_DEFAULT);
                Integer requestCount = safeGetEnvInt(Constant.AGENT_REQUEST_COUNT, Constant.AGENT_REQUEST_COUNT_DEFAULT);
                String loadbalacne = safeGetEnv(Constant.AGENT_LOCADBANCE, Constant.AGENT_LOCADBANCE_DEFAULT);
                String serialization = safeGetEnv(Constant.AGENT_SERIALIZATION, Constant.AGENT_SERIALIZATION_DEFAULT);
                String protocol = safeGetEnv(Constant.AGENT_PROTOCOL, Constant.AGENT_PROTOCOL_DEFAULT);
                String testModeString = safeGetEnv(Constant.AGENT_TEST_MODE, Constant.AGENT_TEST_MODE_DEFAULT);
                TestMode testMode =  TestMode.valueOf(testModeString);
                TestConfig testConfig = new TestConfig();
                testConfig.setProtocol(protocol);
                testConfig.setLocadbance(loadbalacne);
                testConfig.setNamespace(spacename);
                testConfig.setDurationSeconds(durationSeconds);
                testConfig.setRequestCount(requestCount);
                testConfig.setSerialization(serialization);
                testConfig.setTestMode(testMode);
                int port = Integer.parseInt(servicePort);
                nettyServer = new NettyServer(port);
                AgentNettyHandler agentNettyHandler = new AgentNettyHandler(nettyServer, testConfig);
                List<ChannelHandler> customHandlers = Arrays.asList(
                        agentNettyHandler
                );
                nettyServer.setCustomHandlers(customHandlers);
                try {
                    nettyServer.start();
                } catch (Exception e) {
                   throw new Exception("NettyAgent Start fail", e);
                }

            } catch (Exception e) {
                throw new RuntimeException("NettyAgent start fail", e);
            }
        },"netty-server-main");
        nettyServerThread.setDaemon(false);
        nettyServerThread.start();
    }

    @PreDestroy
    public void stop() {;
        if (nettyServer != null) {
            try {
                nettyServer.shutdown();
            } catch (Exception e) {
                throw new RuntimeException("NettyAgent have fail", e);
            }
        }
    }
}
