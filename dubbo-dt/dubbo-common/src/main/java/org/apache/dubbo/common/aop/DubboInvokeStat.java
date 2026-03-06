package org.apache.dubbo.common.aop;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboInvokeStat {
    String namespace() default "";
    String argKey();
    Class<?> argValue();
}