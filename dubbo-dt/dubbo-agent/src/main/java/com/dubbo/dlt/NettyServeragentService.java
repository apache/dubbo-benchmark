package com.dubbo.dlt;

import com.dubbo.common.conf.TestMode;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.dlt.handler.AgentNettyHandler;
import com.dubbo.common.netty.NettyServer;
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
                String spacename = safeGetEnv("SPRING_APPLICATION_NAME", "dubbo-agent");
                String servicePort = safeGetEnv("SERVICE_PORT", "8082");
                Long durationSeconds = safeGetDurationSeconds("AGENT_DURATION_SECONDS", 1000L);
                Integer requestCount = safeGetEnvInt("AGENT_REQUEST_COUNT", 100);
                String loadbalacne = safeGetEnv("AGENT_LOCADBANCE", "random");
                String serialization = safeGetEnv("AGENT_SERIALIZATION", "hessian2");
                String testModeString = safeGetEnv("AGENT_TEST_MODE", "FIXED_COUNT");
                TestMode testMode =  TestMode.valueOf(testModeString);
                TestConfig testConfig = new TestConfig();
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
