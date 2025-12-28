package com.dubbo.provide;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.dubbo.provide","com.dubbo.common"})
@EnableDubbo
public class ProviderApplication {
    
    public static void main(String[] args) {
        // 获取环境变量
        String providerId = System.getenv("PROVIDER_ID");
        String agentHost = System.getenv("AGENT_HOST");
        String agentPort = System.getenv("AGENT_PORT");
        
        System.out.println("========================================");
        System.out.println("启动Dubbo Provider");
        System.out.println("Provider ID: " + providerId);
        System.out.println("Agent地址: " + agentHost + ":" + agentPort);
        System.out.println("========================================");
        
        ConfigurableApplicationContext context = 
            SpringApplication.run(ProviderApplication.class, args);
        
        // 获取Netty客户端服务;;
        NettyProvideService nettyProvideService =
            context.getBean(NettyProvideService.class);
        
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Provider正在关闭...");
            context.close();
        }));
    }
}