package org.apache.dubbo.consumer;

import org.apache.dubbo.common.aop.EnableDubboTest;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = {
        "org.apache.dubbo.consumer",
        "org.apache.dubbo.common"
})
@EnableDubboTest(basePackages = {"org.apache.dubbo.common"}, testModel = "consumer")
public class ConsumerApplication {
    public static void main(String[] args) {
      SpringApplication.run(ConsumerApplication.class, args);
    }
}