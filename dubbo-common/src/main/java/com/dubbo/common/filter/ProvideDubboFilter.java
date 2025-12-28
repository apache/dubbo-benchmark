package com.dubbo.common.filter;


import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.aop.DubboStatManager;
import com.dubbo.common.entry.CallResultManager;
import com.dubbo.common.entry.ProvideResult;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Dubbo统计过滤器
 * 在Dubbo调用链中拦截并记录统计信息
 */
@Activate(group = {CommonConstants.PROVIDER})
public class ProvideDubboFilter implements Filter {
    private final CallResultManager callResultManager = CallResultManager.getInstance();
    private DubboStatManager dubboStatManager =  DubboStatManager.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 1. 获取服务接口
        URL url = invoker.getUrl();

        // 方式1：从URL参数获取
        Class<?> serviceInterface = invoker.getInterface();
        String methodName = invocation.getMethodName();
        String interfaceName = invoker.getInterface().getSimpleName();
        String providerName = interfaceName + "-provider";

        // 2. 查找方法上的注解
        DubboInvokeStat annotation = findAnnotation(serviceInterface, methodName,
                                                    invocation.getParameterTypes());

        // 3. 没有注解，直接调用
        if (annotation == null) {
            return invoker.invoke(invocation);
        }

        // 4. 开始统计
        String error = null;
        ProvideResult provideResult = new ProvideResult();
        provideResult.setServiceName(providerName);
        provideResult.setMethodName(methodName);
        provideResult.setStartTime(new Date(System.currentTimeMillis()));

        try {
            Result result = invoker.invoke(invocation);
            provideResult.setSuccess(true);
            provideResult.setEndTime(new Date(System.currentTimeMillis()));
            callResultManager.beginCall(providerName, provideResult);
            return result;
        } catch (RpcException e) {
            provideResult.setServiceName(providerName);
            provideResult.setMethodName(methodName);
            provideResult.setEndTime(new Date(System.currentTimeMillis()));
            provideResult.setSuccess(false);
            callResultManager.beginCall(providerName, provideResult);
            error = e.getMessage();
            throw e;
        }
    }

    /**
     * 在接口中查找注解
     */
    private DubboInvokeStat findAnnotation(Class<?> serviceInterface,
                                          String methodName,
                                          Class<?>[] paramTypes) {
        try {
            Method method = serviceInterface.getMethod(methodName, paramTypes);
            return method.getAnnotation(DubboInvokeStat.class);
        } catch (NoSuchMethodException e) {
            // 可能方法签名不匹配，尝试名称匹配
            for (Method m : serviceInterface.getMethods()) {
                if (m.getName().equals(methodName)) {
                    DubboInvokeStat annotation = m.getAnnotation(DubboInvokeStat.class);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
            return null;
        }
    }

    private String getStatName(DubboInvokeStat annotation, Class<?> serviceInterface) {
        String name = annotation.value();
        if (!name.isEmpty()) {
            return name;
        }
        return serviceInterface.getSimpleName();
    }
}