package com.dubbo.consumer;



import com.dubbo.common.aop.EnableDubboTest;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.dubbo.common","com.dubbo.consumer"})
@EnableDubbo
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"})
public class ConsumerApplication {

    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = 
            SpringApplication.run(ConsumerApplication.class, args);
        NettyConsumer nettyClientService =
            context.getBean(NettyConsumer.class);

    }
}