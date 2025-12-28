package com.dubbo.common;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleDubboFactory {
    
    @Value("${dubbo.registry.address:nacos://127.0.0.1:8848}")
    private String registryAddress;
    
    @Value("${dubbo.application.name:dubbo-test-consumer}")
    private String applicationName;
    
    // 缓存已创建的代理
    private final Map<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();
    
    /**
     * 创建Dubbo代理的核心方法
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass) {
        // 1. 检查缓存（避免重复创建）
        if (proxyCache.containsKey(interfaceClass)) {
            return (T) proxyCache.get(interfaceClass);
        }
        
        // 2. 加锁，保证线程安全
        synchronized (this) {
            // 双重检查（Double-Check）
            if (proxyCache.containsKey(interfaceClass)) {
                return (T) proxyCache.get(interfaceClass);
            }
            
            try {
                // 3. 实际创建代理
                T proxy = doCreateProxy(interfaceClass);
                
                // 4. 放入缓存
                proxyCache.put(interfaceClass, proxy);
                
                System.out.println("✅ 创建Dubbo代理成功: " + interfaceClass.getSimpleName());
                return proxy;
                
            } catch (Exception e) {
                System.err.println("❌ 创建Dubbo代理失败: " + interfaceClass.getName());
                throw new RuntimeException("创建Dubbo代理失败", e);
            }
        }
    }
    
    /**
     * 实际创建代理的细节
     */
    private <T> T doCreateProxy(Class<T> interfaceClass) {
        System.out.println("=== 开始创建Dubbo代理 ===");
        System.out.println("接口: " + interfaceClass.getName());
        
        // 1. 创建应用配置
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        applicationConfig.setQosEnable(false);  // 关闭QoS
        System.out.println("应用名: " + applicationName);
        
        // 2. 创建注册中心配置
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddress);
        registryConfig.setTimeout(10000);  // 10秒超时
        System.out.println("注册中心: " + registryAddress);
        
        // 3. 创建引用配置（核心）
        ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();
        
        // 设置基本配置
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setInterface(interfaceClass);  // 要调用的接口
        
        // 消费者配置
        referenceConfig.setCheck(false);      // 启动时不检查提供者
        referenceConfig.setLazy(true);        // 延迟连接
        referenceConfig.setGeneric(false);    // 不使用泛化调用
        
        // 超时和重试
        referenceConfig.setTimeout(5000);     // 5秒超时
        referenceConfig.setRetries(0);        // 不重试（测试时建议）
        
        // 负载均衡策略
        referenceConfig.setLoadbalance("roundrobin");  // 轮询
        
        // 协议和序列化
        referenceConfig.setProtocol("dubbo");
        
        System.out.println("配置完成: timeout=" + 5000 + 
                          ", retries=" + 0 + 
                          ", loadbalance=roundrobin");
        
        // 4. 获取代理对象（关键步骤）
        System.out.println("正在获取代理对象...");
        T proxy = referenceConfig.get();  // ← Dubbo在这里创建代理
        
        System.out.println("代理创建完成，类型: " + proxy.getClass().getName());
        System.out.println("=== Dubbo代理创建结束 ===\n");
        
        return proxy;
    }
    
    /**
     * 清理缓存
     */
    public void clearCache() {
        proxyCache.clear();
        System.out.println("Dubbo代理缓存已清理");
    }
}