package com.dubbo.provider;

import com.dubbo.common.aop.EnableDubboTest;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.dubbo.provide","com.dubbo.common"})
@EnableDubbo
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "provider")
public class ProviderApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(com.dubbo.provider.ProviderApplication.class, args);
    }
}