package com.dubbo.consumer;

import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.ControlCommand;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.QoPData;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.NettyClient;
import com.dubbo.common.netty.lister.MessageListener;
import com.dubbo.common.netty.protocol.*;
import com.dubbo.common.scan.SimpleDubboScanner;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NettyConsumer implements MessageListener {

    @Value("${agent.host:localhost}")
    private String agentHost;
    
    @Value("${agent.port:8888}")
    private int agentPort;
    
    @Value("${spring.application.name:2}")
    private String consumerId;
    private TestConfig testConfig = new TestConfig();

    private NettyClient nettyClient;
    private ScheduledExecutorService scheduler;
    @Autowired
    private SimpleDubboScanner simpleDubboScanner;
    private TestExecutor testExecutor = new TestExecutor();
    private volatile boolean isRegistered = false;
    private volatile boolean isRunning = true;

    private Logger logger = LoggerFactory.getLogger(NettyConsumer.class);
    @PostConstruct
    public void init() {
        try {
            // 创建Netty客户端
            nettyClient = new NettyClient(agentHost, agentPort, consumerId);
            testConfig.setConsumerId(consumerId);
            ConsumerHandler consumerHandler = new ConsumerHandler(testExecutor, simpleDubboScanner, testConfig);
            nettyClient.setChannelHandler(Collections.singletonList(consumerHandler));
            
            // 连接到Agent
            connectToAgent();
            
            // 启动定时任务
            startScheduledTasks();

            logger.info("Consumer Netty客户端初始化完成");
            
        } catch (Exception e) {
            logger.error("Consumer Netty客户端初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 连接到Agent
     */
    private void connectToAgent() {
        try {
            logger.info("正在连接到Agent: " + agentHost + ":" + agentPort);
            
            // 连接Agent
            nettyClient.connect();

            // 注册到Agent
            registerToAgent();
            
        } catch (Exception e) {
            logger.error("连接Agent失败: " + e.getMessage());
            scheduleReconnect();
        }
    }
    
    /**
     * 注册到Agent
     */
    private void registerToAgent() {
        logger.info("开始注册到");
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
        logger.info("Consumer已发送注册消息到Agent");
    }
    
    /**
     * 启动定时任务
     */
    private void startScheduledTasks() {
        scheduler = Executors.newScheduledThreadPool(2);
        
        // 定时发送心跳
        scheduler.scheduleAtFixedRate(() -> {
            if (nettyClient.isConnected() && isRegistered) {
                HeartbeatMessage heartbeat = new HeartbeatMessage();
                heartbeat.setClientId("Consumer-" + consumerId);
                // 添加负载信息
                Message message = new Message();
                message.setData(JSONObject.toJSONString(heartbeat));
                message.setType(MessageType.HEARTBEAT);
                message.setTimestamp(System.currentTimeMillis());
                message.setClientId(consumerId);
                nettyClient.sendMessage(message);
            }
        }, 10, 10, TimeUnit.SECONDS);
        
        // 定时发送状态报告（可选）
        scheduler.scheduleAtFixedRate(() -> {
            if (nettyClient.isConnected() && isRegistered) {
                sendStatusReport();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * 发送状态报告
     */
    private void sendStatusReport() {
        // 可以定期发送Consumer的状态信息
        logger.info("发送Consumer状态报告...");
        // 实现状态报告逻辑
    }
    
    /**
     * 消息监听器实现
     */
    @Override
    public void onMessage(Message message) {
        logger.info("Consumer收到消息类型: " + message.getType());
        
        switch (message.getType()) {
            case ACK:
                handleAckMessage((AckMessage) message);
                break;
            case CONTROL:
                handleControlMessage((ControlMessage) message);
                break;
            case SHUTDOWN:
                handleShutdownMessage((ShutdownMessage) message);
                break;
            default:
                logger.info("Consumer收到未处理的消息类型: " + message.getType());
        }
    }
    
    @Override
    public void onControlMessage(ControlMessage message) {
        logger.info("Consumer收到控制指令: " + message.getCommand());
        handleControlMessage(message);
    }
    
    @Override
    public void onShutdown(ShutdownMessage message) {
        logger.info("Consumer收到下线指令");
        handleShutdownMessage(message);
    }
    
    /**
     * 处理ACK消息
     */
    private void handleAckMessage(AckMessage ack) {
        if (ack.isSuccess()) {
            logger.info("Consumer收到ACK: " + ack.getMessage());
            
            if ("注册成功".equals(ack.getMessage())) {
                isRegistered = true;
                logger.info("Consumer成功注册到Agent");
            }
        } else {
            logger.error("Consumer收到错误ACK: " + ack.getMessage());
        }
    }
    
    /**
     * 处理控制消息
     */
    private void handleControlMessage(ControlMessage controlMsg) {
        logger.info("Consumer执行控制指令: " + controlMsg.getCommand());
        
        if (controlMsg.getCommand() == ControlCommand.REQUEST_QOP) {
            handleQoPRequest();
        } else {
            logger.info("Consumer未实现的控制指令: " + controlMsg.getCommand());
        }
    }
    
    /**
     * 处理QOP数据请求
     */
    private void handleQoPRequest() {
        try {
            logger.info("收集QOP数据...");
            
            // 收集QOP数据
            QoPData qoPData = new QoPData();
            
            // 发送QOP消息
            QoPMessage qoPMessage = new QoPMessage();
            qoPMessage.setQoPData(qoPData);
            qoPMessage.setTimestamp(System.currentTimeMillis());
            
            nettyClient.sendMessage(qoPMessage);
            logger.info("已发送QOP数据到Agent");
            
        } catch (Exception e) {
            logger.error("处理QOP请求失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理下线指令
     */
    private void handleShutdownMessage(ShutdownMessage shutdownMsg) {
        logger.info("Consumer收到下线指令，原因: " + shutdownMsg.getReason());
        
        // 发送确认
        AckMessage ack = new AckMessage();
        ack.setRequestId(shutdownMsg.getMessageId());
        ack.setSuccess(true);
        ack.setMessage("收到下线指令，准备关闭");
        ack.setTimestamp(System.currentTimeMillis());
        
        nettyClient.sendMessage(ack);
        
        // 延迟关闭
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        }).start();
    }
    
    /**
     * 获取主机名
     */
    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * 调度重连
     */
    private void scheduleReconnect() {
        logger.info("5秒后尝试重新连接...");
        ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
        reconnectScheduler.schedule(() -> {
            if (isRunning) {
                logger.info("尝试重新连接...");
                connectToAgent();
            }
            reconnectScheduler.shutdown();
        }, 5, TimeUnit.SECONDS);
    }
    
    @PreDestroy
    public void shutdown() {
        logger.info("关闭Consumer Netty客户端...");
        isRunning = false;
        
        // 发送下线消息
        if (nettyClient != null && nettyClient.isConnected()) {
            ShutdownMessage shutdownMsg = new ShutdownMessage();
            shutdownMsg.setReason("Consumer正常关闭");
            shutdownMsg.setTimestamp(System.currentTimeMillis());
            nettyClient.sendMessage(shutdownMsg);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 关闭调度器
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        
        // 关闭Netty客户端
        if (nettyClient != null) {
            nettyClient.disconnect();
        }
        
        logger.info("Consumer Netty客户端已关闭");
    }
}