package com.dubbo.common.netty;


import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.ControlCommand;
import com.dubbo.common.entry.ClientSession;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.decoder.MessageDecoder;
import com.dubbo.common.netty.encoder.MessageEncoder;
import com.dubbo.common.netty.protocol.ControlMessage;
import com.dubbo.common.netty.protocol.ShutdownMessage;
import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Netty服务器（Agent端使用）
 */

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    
    // 客户端连接管理
    private final Map<String, ClientSession> clientSessions = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Message>> pendingRequests = new ConcurrentHashMap<>();
    private List<ChannelHandler> customHandlers;
    public NettyServer(int port) {
        this.port = port;
    }
    public void setCustomHandlers(List<ChannelHandler> customHandlers) {
        this.customHandlers = customHandlers;
    }
    
    /**
     * 启动服务器
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(2);
        workerGroup = new NioEventLoopGroup(2);
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 解决粘包/拆包
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    1024 * 1024, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            
                            // 空闲检测
                            pipeline.addLast(new IdleStateHandler(30, 0, 0));
                            
                            // 编解码器
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());
                            
                            // 业务处理器
                            customHandlers.forEach(handler -> pipeline.addLast((handler)));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            channel = future.channel();

            // 注册关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 注册客户端
     */
    public void registerClient(String clientId, Channel channel, ClientType type) {
        ClientSession session = new ClientSession(clientId, channel, type);
        clientSessions.put(clientId, session);
        logger.info("客户端注册: {} ({})", clientId, type);
    }
    
    /**
     * 注销客户端
     */
    public void unregisterClient(String clientId) {
        clientSessions.remove(clientId);
        logger.info("客户端注销: {}", clientId);
    }
    
    /**
     * 发送消息给指定客户端
     */
    public void sendToClient(String clientId, Message message) {
        ClientSession session = clientSessions.get(clientId);
        if (session != null && session.getChannel().isActive()) {
            session.getChannel().writeAndFlush(message);
        } else {
            logger.warn("客户端未连接或不存在: {}", clientId);
        }
    }
    
    /**
     * 广播消息给所有客户端或者指定的客户端
     */
    public void broadcast(Message message, ClientType filterType) {
        clientSessions.values().stream()
                .filter(session -> filterType == null || session.getType() == filterType)
                .filter(session -> session.getChannel().isActive())
                .forEach(session -> {
                    session.getChannel().writeAndFlush(message);
                });
    }
    
    /**
     * 发送控制指令给Consumer
     */
    public void sendControlToConsumer(String consumerId, ControlCommand command, String data) {
        ControlMessage msg = new ControlMessage();
        msg.setCommand(command);
        msg.setData(data);
        msg.setClientId(consumerId);
        msg.setTimestamp(System.currentTimeMillis());
        sendToClient(consumerId, msg);
    }
    
    /**
     * 请求Consumer开始测试
     */
    public void startTest(String consumerId, TestConfig config) {
        ControlMessage msg = new ControlMessage();
        msg.setCommand(ControlCommand.START_TEST);
        msg.setData(JSONObject.toJSONString(config));
        msg.setTargetClientId(consumerId);
        msg.setTimestamp(System.currentTimeMillis());
        
        sendToClient(consumerId, msg);
    }
    
    /**
     * 请求Consumer停止测试
     */
    public void stopTest(String consumerId) {
        ControlMessage msg = new ControlMessage();
        msg.setCommand(ControlCommand.STOP_TEST);
        msg.setTargetClientId(consumerId);
        msg.setTimestamp(System.currentTimeMillis());
        
        sendToClient(consumerId, msg);
    }
    
    /**
     * 请求Provider发送QOP数据
     */
    public void requestQoPData(String providerId) {
        ControlMessage msg = new ControlMessage();
        msg.setCommand(ControlCommand.REQUEST_QOP);
        msg.setTargetClientId(providerId);
        msg.setTimestamp(System.currentTimeMillis());
        
        sendToClient(providerId, msg);
    }
    
    /**
     * 发送下线指令
     */
    public void sendShutdown(String clientId) {
        ShutdownMessage msg = new ShutdownMessage();
        msg.setTargetClientId(clientId);
        msg.setTimestamp(System.currentTimeMillis());
        
        sendToClient(clientId, msg);
    }
    
    /**
     * 关闭服务器
     */
    public void shutdown() {
        // 通知所有客户端
        broadcast(new ShutdownMessage(), null);
        
        if (channel != null) {
            channel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        
        logger.info("Netty服务器已关闭");
    }
    
    /**
     * 获取客户端会话
     */
    public ClientSession getClientSession(String clientId) {
        return clientSessions.get(clientId);
    }
    
    /**
     * 获取所有客户端会话
     */
    public Map<String, ClientSession> getAllSessions() {
        return clientSessions;
    }
    
    /**
     * 等待响应
     */
    public CompletableFuture<Message> waitForResponse(String requestId, long timeoutMs) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        
        // 超时处理
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(timeoutMs);
                if (!future.isDone()) {
                    future.completeExceptionally(new TimeoutException("等待响应超时"));
                    pendingRequests.remove(requestId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        return future;
    }
    
    /**
     * 完成响应
     */
    public void completeResponse(String requestId, Message response) {
        CompletableFuture<Message> future = pendingRequests.remove(requestId);
        if (future != null) {
            future.complete(response);
        }
    }
}