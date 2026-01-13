package com.dubbo.common.netty;

import com.dubbo.common.entry.Message;
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
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());
                        channelHandler.forEach(channel -> {pipeline.addLast(channel);});
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
    }

    public void sendMessage(Message message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("Message send fail: {}", future.cause().getMessage());
                }
            });
        } else {
            logger.warn("not connect，not send message");
        }
    }


    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        reconnectScheduler.shutdown();
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}