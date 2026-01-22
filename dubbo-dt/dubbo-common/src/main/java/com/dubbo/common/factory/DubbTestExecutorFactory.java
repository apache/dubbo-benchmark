package com.dubbo.common.factory;

import java.lang.reflect.Method;

public interface DubbTestExecutorFactory {
    void beforeInvokeFilter(Method method, Object[] args, Class<?> serviceInterface);
    void afterInvokeFilter(Method method, Object[] args, Object result, Class<?> serviceInterface);
    void exceptionInvokeFilter(Method method, String argValue, Exception e);
}
