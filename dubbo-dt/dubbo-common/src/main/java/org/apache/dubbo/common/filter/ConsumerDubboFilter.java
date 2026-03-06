package org.apache.dubbo.common.filter;

import org.apache.dubbo.common.aop.DubboInvokeStat;
import org.apache.dubbo.common.entry.ConsumerTestResult;
import org.apache.dubbo.common.entry.TestConfig;
import org.apache.dubbo.common.entry.TestStatContext;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Activate(group = {CommonConstants.CONSUMER}, order = 100)
public class ConsumerDubboFilter implements Filter {

    private static final Map<String, TestStatContext> testContexts = new ConcurrentHashMap<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
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
            if (result.hasException()) {
                testContext.recordFailure();
                testContext.decreaseConcurrent();
                return result;
            }
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