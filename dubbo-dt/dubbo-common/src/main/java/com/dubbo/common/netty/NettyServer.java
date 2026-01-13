package com.dubbo.common.netty;


import com.dubbo.common.conf.ClientType;
import com.dubbo.common.entry.ClientSession;
import com.dubbo.common.entry.Message;
import com.dubbo.common.netty.decoder.MessageDecoder;
import com.dubbo.common.netty.encoder.MessageEncoder;
import com.dubbo.common.netty.protocol.ShutdownMessage;
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


public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    private final Map<String, ClientSession> clientSessions = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Message>> pendingRequests = new ConcurrentHashMap<>();
    private List<ChannelHandler> customHandlers;
    public NettyServer(int port) {
        this.port = port;
    }
    public void setCustomHandlers(List<ChannelHandler> customHandlers) {
        this.customHandlers = customHandlers;
    }

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

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    1024 * 1024, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            pipeline.addLast(new IdleStateHandler(30, 0, 0));
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());
                            customHandlers.forEach(handler -> pipeline.addLast((handler)));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            channel = future.channel();
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void broadcast(Message message, ClientType filterType) {
        clientSessions.values().stream()
                .filter(session -> filterType == null || session.getType() == filterType)
                .filter(session -> session.getChannel().isActive())
                .forEach(session -> {
                    session.getChannel().writeAndFlush(message);
                });
    }

    public void shutdown() {
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

    }
    public Map<String, ClientSession> getAllSessions() {
        return clientSessions;
    }
}