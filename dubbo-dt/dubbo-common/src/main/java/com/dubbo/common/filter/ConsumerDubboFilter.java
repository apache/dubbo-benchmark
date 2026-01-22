package com.dubbo.common.filter;

import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.entry.TestStatContext;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Activate(group = {CommonConstants.CONSUMER}, order = 100)
public class ConsumerDubboFilter implements Filter {

    private static final Map<String, TestStatContext> testContexts = new ConcurrentHashMap<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        boolean hasStatAnnotation = hasDubboInvokeStatAnnotation(invoker, invocation);
        if (!hasStatAnnotation) {
            return invoker.invoke(invocation);
        }
        return executeWithTestStat(invoker, invocation);
    }

    public static TestStatContext startTest(String consumerId) {
        TestStatContext context = new TestStatContext(consumerId);
        testContexts.put(consumerId, context);
        return context;
    }

    public static ConsumerTestResult endTest(TestConfig config, String consumerId) {
        TestStatContext context = testContexts.remove(consumerId);
        if (context != null) {
            context.endTest();
            ConsumerTestResult testResult = context.toTestResult();
            testResult.setFailedRequests(config.getRequestCount() - testResult.getSuccessfulRequests());
            testResult.setTotalRequests(config.getRequestCount());
            return testResult;
        }
        return null;
    }

    public static TestStatContext getTestContext(String consumerId) {
        return testContexts.get(consumerId);
    }

    private boolean hasDubboInvokeStatAnnotation(Invoker<?> invoker, Invocation invocation) {
        try {
            Class<?> serviceInterface = invoker.getInterface();
            String methodName = invocation.getMethodName();
            Class<?>[] paramTypes = invocation.getParameterTypes();

            Method method = serviceInterface.getMethod(methodName, paramTypes);
            return method.isAnnotationPresent(DubboInvokeStat.class);
        } catch (NoSuchMethodException e) {
            for (Method m : invoker.getInterface().getMethods()) {
                if (m.getName().equals(invocation.getMethodName()) &&
                        m.getParameterCount() == invocation.getArguments().length) {
                    return m.isAnnotationPresent(DubboInvokeStat.class);
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Result executeWithTestStat(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String consumerId = invocation.getMethodName();
        TestStatContext testContext = testContexts.get(consumerId);
        if (testContext == null) {
            return invoker.invoke(invocation);
        }

        testContext.increaseConcurrent();
        long startTime = System.currentTimeMillis();
        String providerName = "unknown-provider";
        Result result = null;
        try {
            result = invoker.invoke(invocation);
            RpcContext rpcContext = RpcContext.getContext();
            URL url = rpcContext.getUrl();
            if (url != null) {
                providerName = url.getRemoteApplication();
                if (providerName == null || providerName.isEmpty()) {
                    providerName = url.getAddress();
                }
            }
            long responseTime = System.currentTimeMillis() - startTime;
            testContext.recordSuccess(responseTime);
            testContext.recordProvider(providerName + "#" + invocation.getMethodName());
            return result;
        } catch (Exception e) {
            testContext.recordFailure();
            throw new RpcException("DubboRpc exe fail", e);
        } finally {
            testContext.decreaseConcurrent();
        }
    }
}