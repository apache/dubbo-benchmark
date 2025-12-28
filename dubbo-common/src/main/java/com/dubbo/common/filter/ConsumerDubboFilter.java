package com.dubbo.common.filter;

import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.entry.CallResultManager;
import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.ProvideResult;
import com.dubbo.common.entry.TestStatContext;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Activate(group = {CommonConstants.CONSUMER})
public class ConsumerDubboFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDubboFilter.class);

    private static final Map<String, TestStatContext> testContexts = new ConcurrentHashMap<>();

    private static final ThreadLocal<String> currentTestId = new ThreadLocal<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        boolean hasStatAnnotation = hasDubboInvokeStatAnnotation(invoker, invocation);


        if (!hasStatAnnotation) {
            return invoker.invoke(invocation);
        }

        // 3. 获取测试上下文（如果有测试在进行）
        String testId = currentTestId.get();
        if (testId == null) {
            // 没有测试在进行，只记录普通的调用统计
            return executeWithBasicStat(invoker, invocation);
        }

        // 4. 有测试在进行，执行测试统计
        return executeWithTestStat(invoker, invocation, testId);
    }

    /**
     * 设置当前线程的测试ID
     */
    public static void setCurrentTest(String testId) {
        if (testId != null) {
            currentTestId.set(testId);
        }
    }

    /**
     * 清除当前线程的测试ID
     */
    public static void clearCurrentTest() {
        currentTestId.remove();
    }

    /**
     * 开始一个新的测试
     */
    public static TestStatContext startTest(String testId, String consumerId) {
        TestStatContext context = new TestStatContext(testId, consumerId);
        testContexts.put(testId, context);
        return context;
    }

    /**
     * 结束测试并获取结果
     */
    public static ConsumerTestResult endTest(String testId) {
        TestStatContext context = testContexts.remove(testId);
        if (context != null) {
            context.endTest();
            return context.toTestResult();
        }
        return null;
    }

    /**
     * 获取测试上下文
     */
    public static TestStatContext getTestContext(String testId) {
        return testContexts.get(testId);
    }

    /**
     * 检查方法是否有@DubboInvokeStat注解
     */
    private boolean hasDubboInvokeStatAnnotation(Invoker<?> invoker, Invocation invocation) {
        try {
            Class<?> serviceInterface = invoker.getInterface();
            String methodName = invocation.getMethodName();
            Class<?>[] paramTypes = invocation.getParameterTypes();

            Method method = serviceInterface.getMethod(methodName, paramTypes);
            return method.isAnnotationPresent(DubboInvokeStat.class);
        } catch (NoSuchMethodException e) {
            // 如果没有精确匹配的方法，尝试模糊匹配
            for (Method m : invoker.getInterface().getMethods()) {
                if (m.getName().equals(invocation.getMethodName()) &&
                        m.getParameterCount() == invocation.getArguments().length) {
                    return m.isAnnotationPresent(DubboInvokeStat.class);
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("检查注解失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 执行带有基本统计的调用
     */
    private Result executeWithBasicStat(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 1. 记录开始时间
        long startTime = System.currentTimeMillis();

        // 记录方法信息（调用前可以获取的）

        try {
            // 2. 执行调用
            Result result = invoker.invoke(invocation);

            // 3. 调用成功后获取实际的provider信息
            RpcContext rpcContext = RpcContext.getContext();
            String actualProviderName = rpcContext.getRemoteApplicationName();

            // 4. 记录成功调用
            long responseTime = System.currentTimeMillis() - startTime;

            return result;
        } catch (Exception e) {

            throw e;
        }
    }

    /**
     * 执行带有测试统计的调用
     */
    private Result executeWithTestStat(Invoker<?> invoker, Invocation invocation, String testId)
            throws RpcException {

        // 1. 获取测试上下文
        TestStatContext testContext = testContexts.get(testId + invocation.getMethodName());
        testContext.increaseConcurrent();
        if (testContext == null) {
            // 测试上下文不存在，回退到基本统计
            return executeWithBasicStat(invoker, invocation);
        }

        // 2. 记录开始时间
        long startTime = System.currentTimeMillis();
        String providerName = null;

        try {
            // 3. 执行调用
            Result result = invoker.invoke(invocation);
            testContext.decreaseConcurrent();
            // 调用成功后获取provider信息
            RpcContext rpcContext = RpcContext.getServerContext();
            URL url = rpcContext.getUrl();
            providerName = url.getRemoteApplication();

            // 5. 记录成功调用
            long responseTime = System.currentTimeMillis() - startTime;

            // 记录到测试上下文
            testContext.recordSuccess(responseTime);
            testContext.recordProvider(providerName + invocation.getMethodName());

            log.debug("测试调用成功 - 测试ID: {}, Provider: {}, 耗时: {}ms",
                    testId, providerName, responseTime);

            return result;
        } catch (Exception e) {
            // 错误记录
            testContext.recordFailure();
            testContext.decreaseConcurrent();
            testContext.recordProvider(providerName);
            log.debug("测试调用失败 - 测试ID: {}, Provider: {}, 错误: {}",
                    testId, providerName, e.getMessage());

            throw e;
        }
    }



    /**
     * 获取所有invoker（通过反射，因为getAllInvokers可能是protected）
     */
    private List<Invoker<?>> getAllInvokers(Object directory) {
        try {
            // 尝试调用getAllInvokers方法
            Method method = directory.getClass().getMethod("getAllInvokers");
            method.setAccessible(true);
            Object result = method.invoke(directory);

            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<Invoker<?>> invokers = (List<Invoker<?>>) result;
                return invokers;
            }
        } catch (NoSuchMethodException e) {
            log.debug("directory没有getAllInvokers方法: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("调用getAllInvokers失败: {}", e.getMessage());
        }

        // 尝试其他可能的方法名
        try {
            Method method = directory.getClass().getMethod("getInvokers");
            method.setAccessible(true);
            Object result = method.invoke(directory);

            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<Invoker<?>> invokers = (List<Invoker<?>>) result;
                return invokers;
            }
        } catch (Exception e) {
            log.debug("调用getInvokers失败: {}", e.getMessage());
        }

        return Collections.emptyList();
    }

    /**
     * 获取Provider信息（备用方法，不再主要使用）
     * 保留这个方法是为了兼容性
     */
    private ProvideResult getProviderServiceName(Invoker<?> invoker) {
        ProvideResult provideResult = new ProvideResult();

        // 尝试从RpcContext获取
        RpcContext rpcContext = RpcContext.getContext();
        String providerApp = rpcContext.getRemoteApplicationName();
        String interfaceName = null;
        String methodName = null;

        // 如果获取不到，从invoker获取
        if (providerApp == null || providerApp.isEmpty()) {

            // 获取接口名
            URL url = invoker.getUrl();
            interfaceName = url != null ? url.getServiceInterface() : null;

            // 如果没有URL，从接口类获取
            if (interfaceName == null || interfaceName.isEmpty()) {
                Class<?> interfaceClass = invoker.getInterface();
                if (interfaceClass != null) {
                    interfaceName = interfaceClass.getSimpleName();
                }
            }
        } else {
            // 如果能从RpcContext获取，也获取接口和方法信息
            interfaceName = rpcContext.getUrl() != null ?
                    rpcContext.getUrl().getServiceInterface() : null;
            methodName = rpcContext.getMethodName();
        }

        provideResult.setServiceName(providerApp != null ? providerApp : "unknown");
        provideResult.setMethodName(methodName != null ? methodName :
                (interfaceName != null ? interfaceName : "unknown"));

        return provideResult;
    }
}