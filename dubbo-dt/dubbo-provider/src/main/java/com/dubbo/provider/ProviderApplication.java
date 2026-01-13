package com.dubbo.provider;

import com.dubbo.common.aop.EnableDubboTest;
import com.dubbo.common.cpu.SystemMonitorUtil;
import com.dubbo.common.provider.NettyProvider;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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