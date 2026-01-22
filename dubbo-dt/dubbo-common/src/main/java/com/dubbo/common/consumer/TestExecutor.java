package com.dubbo.common.consumer;

import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.factory.DubboExecutorFactory;
import com.dubbo.common.filter.ConsumerDubboFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TestExecutor {

    @FunctionalInterface
    public interface TestInvoker {
        void invoke(int requestIndex) throws Exception;
    }
    private static final Logger  logger = LoggerFactory.getLogger(TestExecutor.class);

    DubboExecutorFactory dubboExecutorFactory = new DubboExecutorFactory();
    private  boolean isRunning = false;
    public List<ConsumerTestResult> executeTest(TestConfig config, List<Method> dubboMethods) throws Exception {
        for  (Method dubboMethod : dubboMethods) {
            ConsumerDubboFilter.startTest(dubboMethod.getName());
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
                    throw new UnsupportedOperationException("not support test mode： " + config.getTestMode());
            }
        } finally {
            isRunning = false;
        }
        List<ConsumerTestResult> results = new ArrayList<>();
        for  (Method dubboMethod : dubboMethods) {
            results.add(ConsumerDubboFilter.endTest(config, dubboMethod.getName()));
        }
        return results;
    }

    private void executeFixedCountTest(TestConfig config, List<Method> dubboMothod) {
        int cpuCore = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCore * 2;
        int totalRequestCount = config.getRequestCount();
        ExecutorService threadPool = new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(totalRequestCount),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 0; i < totalRequestCount && isRunning; i++) {
            final int taskIndex = i;
            threadPool.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    dubboExecutorFactory.executeAllMethods(config, dubboMothod);
                    controlRequestRate(config, taskIndex, startTime);
                } catch (Exception e) {
                    logger.error("Dubbo exe error :{}", taskIndex, e);
                }
            });
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(300, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
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

    private long calculateRequestInterval(TestConfig config) {
        if (config.getQpsLimit() != null && config.getQpsLimit() > 0) {
            long interval = 1000L / config.getQpsLimit();
            return Math.max(interval, 1);
        }
        return 10;
    }
}