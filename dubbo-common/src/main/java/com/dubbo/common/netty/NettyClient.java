package com.dubbo.common.netty;

import com.dubbo.common.conf.ClientType;
import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.QoPData;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.decoder.MessageDecoder;
import com.dubbo.common.netty.encoder.MessageEncoder;
import com.dubbo.common.netty.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Netty客户端
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final String host;
    private final int port;
    private final String clientId;

    private EventLoopGroup group;
    private Channel channel;
    private List<ChannelHandler> channelHandler;

    private final ScheduledExecutorService reconnectScheduler =
            Executors.newSingleThreadScheduledExecutor();

    public NettyClient(String host, int port, String clientId) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
    }
    public void setChannelHandler(List<ChannelHandler> channelHandler) {
        this.channelHandler = channelHandler;
    }

    /**
     * 连接到服务器
     */
    public void connect() throws InterruptedException {
        if (channel != null && channel.isActive()) {
            return;
        }

        group = new NioEventLoopGroup(1);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                1024 * 1024, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));

                        pipeline.addLast(new IdleStateHandler(0, 15, 0));

                        // 编解码器
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());

                        // 业务处理器
                        channelHandler.forEach(channel -> {pipeline.addLast(channel);});
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
        logger.info("是否活跃: {}",channel.isActive());
        logger.info("Netty客户端连接成功: {}:{}", host, port);
    }

    /**
     * 向服务器注册
     */
    private void registerToServer() {
        RegisterMessage registerMsg = new RegisterMessage();
        registerMsg.setClientId(clientId);
        registerMsg.setClientType(ClientType.CONSUMER);
        registerMsg.setTimestamp(System.currentTimeMillis());

        sendMessage(registerMsg);
    }

    /**
     * 发送消息
     */
    public void sendMessage(Message message) {
        logger.info("是否活跃: {}",channel.isActive());
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("消息发送失败: {}", future.cause().getMessage());
                }
            });
        } else {
            logger.warn("连接未建立，无法发送消息");
        }
    }

    /**
     * 发送测试准备好消息
     */
    public void sendReady(String testId, TestConfig config) {
        ReadyMessage readyMsg = new ReadyMessage();
        readyMsg.setClientId(clientId);
        readyMsg.setTestId(testId);
        readyMsg.setTestConfig(config);
        readyMsg.setTimestamp(System.currentTimeMillis());

        sendMessage(readyMsg);
    }

    /**
     * 发送测试结果
     */
    public void sendResult(String testId, ConsumerTestResult result) {
        ResultMessage resultMsg = new ResultMessage();
        resultMsg.setClientId(clientId);
        resultMsg.setTestId(testId);
        resultMsg.setTestResult(result);
        resultMsg.setTimestamp(System.currentTimeMillis());
        sendMessage(resultMsg);
    }

    /**
     * 发送QOP数据
     */
    public void sendQoPData(QoPData qoPData) {
        QoPMessage qoPMessage = new QoPMessage();
        qoPMessage.setClientId(clientId);
        qoPMessage.setQoPData(qoPData);
        qoPMessage.setTimestamp(System.currentTimeMillis());

        sendMessage(qoPMessage);
    }

    /**
     * 发送下线请求
     */
    public void sendShutdown() {
        ShutdownMessage shutdownMsg = new ShutdownMessage();
        shutdownMsg.setClientId(clientId);
        shutdownMsg.setTimestamp(System.currentTimeMillis());

        sendMessage(shutdownMsg);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        reconnectScheduler.shutdown();
        logger.info("Netty客户端已断开连接");
    }

    /**
     * 是否连接
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}