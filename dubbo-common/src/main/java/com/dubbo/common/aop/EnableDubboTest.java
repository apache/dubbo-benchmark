package com.dubbo.common.aop;

import java.lang.annotation.*;

/**
 * 启用Dubbo测试框架注解
 * 添加到Spring Boot启动类上，自动扫描并初始化Dubbo测试环境
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableDubboTest {
    
    /**
     * 扫描的基础包路径
     * 默认扫描当前类所在的包及其子包
     */
    String[] basePackages() default {};
    
    /**
     * 扫描的注解类型
     * 默认扫描所有带有@DubboTestMethod注解的接口
     */
    Class<? extends Annotation>[] annotationTypes() default {DubboInvokeStat.class};
    
    /**
     * 是否启用自动扫描
     */
    boolean enableAutoScan() default true;
    
    /**
     * 是否自动创建代理
     */
    boolean autoCreateProxy() default true;
    
    /**
     * Dubbo注册中心地址
     */
    String registryAddress() default "nacos://127.0.0.1:8848";
    
    /**
     * 应用名称
     */
    String applicationName() default "dubbo-test-framework";
    
    /**
     * 是否启用REST API
     */
    boolean enableRestApi() default true;
    
    /**
     * REST API路径前缀
     */
    String apiPath() default "/api/dubbo-test";
    
    /**
     * 是否启用健康检查
     */
    boolean enableHealthCheck() default true;
    
    /**
     * 扫描排除的包
     */
    String[] excludePackages() default {};
    
    /**
     * 扫描排除的类
     */
    Class<?>[] excludeClasses() default {};
}