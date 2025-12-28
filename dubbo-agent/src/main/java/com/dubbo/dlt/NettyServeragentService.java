package com.dubbo.dlt;


import com.dubbo.dlt.handler.AgentNettyHandler;
import com.dubbo.common.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;

public class NettyServeragentService {
    private static final Logger logger = LoggerFactory.getLogger(NettyServeragentService.class);
    String nettyPortTest = System.getProperty("agent.netty.port", "8888");
    int nettyPort = Integer.parseInt(nettyPortTest);

    private Thread nettyServerThread;
    private NettyServer nettyServer;
    
    @PostConstruct
    public void start() {
        nettyServerThread = new Thread(() -> {
            try {
                nettyServer = new NettyServer(8888);
                AgentNettyHandler agentNettyHandler = new AgentNettyHandler(nettyServer);
                nettyServer.setCustomHandlers(Collections.singletonList(agentNettyHandler));
                // 在新线程中启动服务器
                try {
                    nettyServer.start();
                } catch (Exception e) {
                    logger.error("Netty服务器启动失败", e);
                }

                logger.info("Netty服务器启动成功");

            } catch (Exception e) {
                logger.error("Netty服务器启动失败", e);
                throw new RuntimeException("Netty服务器启动失败", e);
            }
        },"netty-server-main");
        nettyServerThread.setDaemon(false);
        nettyServerThread.start();
    }
    @PreDestroy
    public void stop() {
        logger.info("停止Netty服务器...");
        
        if (nettyServer != null) {
            try {
                nettyServer.shutdown();
            } catch (Exception e) {
                logger.error("停止Netty服务器时出错", e);
            }
        }
        logger.info("Netty服务器已停止");
    }


}