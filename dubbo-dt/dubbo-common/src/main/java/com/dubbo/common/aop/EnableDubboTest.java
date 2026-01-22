package com.dubbo.common.aop;

import com.dubbo.common.consumer.NettyConsumer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DubboTestImportSelector.class)
public @interface EnableDubboTest {
    String testModel() default "ALL";

    String[] basePackages() default {};

    Class<? extends Annotation>[] annotationTypes() default {DubboInvokeStat.class};

}