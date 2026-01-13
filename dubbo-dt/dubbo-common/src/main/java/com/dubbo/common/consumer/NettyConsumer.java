package com.dubbo.common.consumer;

import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.NettyClient;
import com.dubbo.common.netty.protocol.*;
import com.dubbo.common.scan.SimpleDubboScanner;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class NettyConsumer{

    private String agentHost;
    private int agentPort;

    private String consumerId;
    private TestConfig testConfig = new TestConfig();

    private NettyClient nettyClient;
    private ScheduledExecutorService scheduler;
    @Autowired
    private SimpleDubboScanner simpleDubboScanner;
    private TestExecutor testExecutor = new TestExecutor();
    private volatile boolean isRegistered = false;
    private volatile boolean isRunning = true;

    private String getSafeEnvString(String envKey, String defaultValue) {
        try {
            String value = System.getenv(envKey);
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return value.trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int getSafeEnvInt(String envKey, int defaultValue) {
        try {
            String value = System.getenv(envKey);
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }

            String trimmedValue = value.trim();
            return Integer.parseInt(trimmedValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @PostConstruct
    public void init() {
        try {
            initializeEnvironmentVariables();
            nettyClient = new NettyClient(agentHost, agentPort, consumerId);
            testConfig.setConsumerId(consumerId);
            ConsumerHandler consumerHandler = new ConsumerHandler(testExecutor, simpleDubboScanner, testConfig);
            nettyClient.setChannelHandler(Collections.singletonList(consumerHandler));
            connectToAgent();
            startScheduledTasks();
        } catch (Exception e) {
            throw new RuntimeException("Consumer Fail", e);
        }
    }

    private void initializeEnvironmentVariables() {
        this.agentHost = getSafeEnvString("AGENT_HOST", "localhost");
        this.consumerId = getSafeEnvString("SPRING_APPLICATION_NAME", "dubbo-consumer");
        this.agentPort = getSafeEnvInt("AGENT_PORT", 8082);
    }

    private void connectToAgent() {
        try {
            nettyClient.connect();
            registerToAgent();

        } catch (Exception e) {
            scheduleReconnect();
        }
    }

    private void registerToAgent() {
        RegisterMessage registerMsg = new RegisterMessage();
        registerMsg.setTimestamp(System.currentTimeMillis());
        Message message = new Message();
        message.setData(JSONObject.toJSONString(registerMsg));
        message.setType(MessageType.REGISTER);
        message.setTimestamp(System.currentTimeMillis());
        message.setClientId(consumerId);
        message.setClientType(ClientType.CONSUMER);
        nettyClient.sendMessage(message);
        isRegistered = true;
    }

    private void startScheduledTasks() {
        scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            if (nettyClient.isConnected() && isRegistered) {
                HeartbeatMessage heartbeat = new HeartbeatMessage();
                Message message = new Message();
                message.setData(JSONObject.toJSONString(heartbeat));
                message.setType(MessageType.HEARTBEAT);
                message.setTimestamp(System.currentTimeMillis());
                message.setClientId(consumerId);
                nettyClient.sendMessage(message);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void scheduleReconnect() {
        ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
        reconnectScheduler.schedule(() -> {
            if (isRunning) {
                connectToAgent();
            }
            reconnectScheduler.shutdown();
        }, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        isRunning = false;
        if (nettyClient != null && nettyClient.isConnected()) {
            ShutdownMessage shutdownMsg = new ShutdownMessage();
            shutdownMsg.setReason("Consumer shutdown");
            shutdownMsg.setTimestamp(System.currentTimeMillis());
            nettyClient.sendMessage(shutdownMsg);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        if (nettyClient != null) {
            nettyClient.disconnect();
        }
    }
}
