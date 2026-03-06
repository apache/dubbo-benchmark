package org.apache.dubbo.provider;

import org.apache.dubbo.common.aop.EnableDubboTest;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"org.apache.dubbo.common"})
@EnableDubbo
@EnableDubboTest(basePackages = {"org.apache.dubbo.common"}, testModel = "provider")
public class ProviderApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProviderApplication.class, args);
    }
}