package org.apache.dubbo.common.filter;


import org.apache.dubbo.common.aop.DubboInvokeStat;
import org.apache.dubbo.common.entry.CallResultManager;
import org.apache.dubbo.common.entry.ProduceResult;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.util.Date;


@Activate(group = {CommonConstants.PROVIDER})
public class ProduceDubboFilter implements Filter {
    private final CallResultManager callResultManager = CallResultManager.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        String interfaceName = invoker.getInterface().getSimpleName();
        String providerName = interfaceName + "-provider";
        String error = null;
        ProduceResult produceResult = new ProduceResult();
        produceResult.setServiceName(providerName);
        produceResult.setMethodName(methodName);
        produceResult.setStartTime(new Date(System.currentTimeMillis()));

        try {
            Result result = invoker.invoke(invocation);
            produceResult.setSuccess(true);
            produceResult.setEndTime(new Date(System.currentTimeMillis()));
            callResultManager.beginCall(providerName, produceResult);
            return result;
        } catch (RpcException e) {
            produceResult.setServiceName(providerName);
            produceResult.setMethodName(methodName);
            produceResult.setEndTime(new Date(System.currentTimeMillis()));
            produceResult.setSuccess(false);
            callResultManager.beginCall(providerName, produceResult);
            error = e.getMessage();
            throw e;
        }
    }

    private DubboInvokeStat findAnnotation(Class<?> serviceInterface,
                                          String methodName,
                                          Class<?>[] paramTypes) {
        try {
            Method method = serviceInterface.getMethod(methodName, paramTypes);
            return method.getAnnotation(DubboInvokeStat.class);
        } catch (NoSuchMethodException e) {
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
}