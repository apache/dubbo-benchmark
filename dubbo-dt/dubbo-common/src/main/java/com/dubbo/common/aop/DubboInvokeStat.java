package com.dubbo.common.aop;

import com.dubbo.common.constant.DubboInvokeEnum;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboInvokeStat {
    String namespace() default "";
    String argKey();
    Class<?> argValue();
}