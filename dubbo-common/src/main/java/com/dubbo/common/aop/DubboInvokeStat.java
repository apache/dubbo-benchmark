package com.dubbo.common.aop;

import com.dubbo.common.constant.DubboInvokeEnum;

import java.lang.annotation.*;

/**
 * Dubbo服务调用统计注解
 * 放在需要统计的Dubbo服务方法上
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboInvokeStat {
    
    /**
     * 统计名称，用于标识不同的统计维度
     */
    String value() default "";

    /**
     *测试参数默认值
     */
    DubboInvokeEnum argValue();
}