package com.dubbo.provide;

import com.dubbo.common.aop.DubboStatManager;
import com.dubbo.common.api.TestService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@DubboService
public class TestServiceImpl implements TestService {

    @Value("${provider.id:1}")
    private String providerId;
    @Autowired
    private DubboStatManager statManager;
    @Override
    public String sayHello(String name) {
        // 模拟业务处理时间
        try {
            int processTime = (int) (Math.random() * 50);
            Thread.sleep(processTime);
            return String.format("Hello %s from Provider-%s", name, providerId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }

    @Override
    public String sayHello2(String name) {
        return name + "你好好呀";
    }

    @Override
    public String sayHeelow(String name) {
        return "";
    }
}