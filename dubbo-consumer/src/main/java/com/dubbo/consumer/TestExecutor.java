package com.dubbo.consumer;

import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.factory.DubboExecutorFactory;
import com.dubbo.common.filter.ConsumerDubboFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TestExecutor {

    // 定义测试执行器
    @FunctionalInterface
    public interface TestInvoker {
        void invoke(int requestIndex) throws Exception;
    }

    /**
     * 执行测试 - 函数式版本
     */
    DubboExecutorFactory dubboExecutorFactory = new DubboExecutorFactory();
    private  boolean isRunning = false;
    public List<ConsumerTestResult> executeTest(TestConfig config, List<Method> dubboMethods) throws Exception {
        String testId = generateTestId();
        for  (Method dubboMethod : dubboMethods) {
            ConsumerDubboFilter.startTest(testId + dubboMethod.getName(), config.getConsumerId()+dubboMethod.getName());
            ConsumerDubboFilter.setCurrentTest(testId);
        }

        try {
            isRunning = true;

            switch (config.getTestMode()) {
                case FIXED_COUNT:
                    executeFixedCountTest(config, dubboMethods);
                    break;
                case DURATION:
                    executeDurationTest(config, dubboMethods);
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的测试模式: " + config.getTestMode());
            }
        } finally {
            ConsumerDubboFilter.clearCurrentTest();
            isRunning = false;
        }
        List<ConsumerTestResult> results = new ArrayList<>();
        for  (Method dubboMethod : dubboMethods) {
            results.add(ConsumerDubboFilter.endTest(testId + dubboMethod.getName()));
        }
        return results;
    }

    private void executeFixedCountTest(TestConfig config, List<Method> dubboMothod) {

        for (int i = 0; i < config.getRequestCount() && isRunning; i++) {
            try {
                long startTime = System.currentTimeMillis();
                dubboExecutorFactory.executeAllMethods(config ,dubboMothod);
                controlRequestRate(config, i, startTime);
            } catch (Exception e) {
            }
        }
    }

    private void executeDurationTest(TestConfig config, List<Method> dubboMothod) {
        long endTime = System.currentTimeMillis() + (config.getDurationSeconds() * 1000L);
        AtomicInteger requestCount = new AtomicInteger(0);

        while (System.currentTimeMillis() < endTime && isRunning) {
            try {
                long startTime = System.currentTimeMillis();

                dubboExecutorFactory.executeAllMethods(config, dubboMothod);

                long sleepTime = calculateRequestInterval(config);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }

            } catch (Exception e) {
            } finally {
                requestCount.incrementAndGet();
            }
        }
    }
    /**
     * 生成测试ID
     */
    private String generateTestId() {
        return "TEST-" + System.currentTimeMillis() + "-" +
                Thread.currentThread().getId();
    }
    /**
     * 控制请求速率
     */
    private void controlRequestRate(TestConfig config, int requestIndex, long startTime) {
        if (config.getQpsLimit() != null && config.getQpsLimit() > 0) {
            long expectedNextTime = startTime + (1000L / config.getQpsLimit() * (requestIndex + 1));
            long sleepTime = expectedNextTime - System.currentTimeMillis();

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 计算请求间隔
     */
    private long calculateRequestInterval(TestConfig config) {
        if (config.getQpsLimit() != null && config.getQpsLimit() > 0) {
            long interval = 1000L / config.getQpsLimit();
            return Math.max(interval, 1);
        }
        return 10; // 默认100 QPS
    }
}