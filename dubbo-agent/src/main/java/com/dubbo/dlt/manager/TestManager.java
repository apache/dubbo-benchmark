package com.dubbo.dlt.manager;


import com.dubbo.dlt.NettyServeragentService;
import com.dubbo.common.conf.TestMode;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.entry.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TestManager {
    private static final Logger logger = LoggerFactory.getLogger(TestManager.class);

    @Autowired
    private NettyServeragentService nettyServerService;


    private final Map<String, TestSession> testSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final Map<String, Boolean> consumerReadyStatus = new ConcurrentHashMap<>();
    private final Map<String, Boolean> providerRegisteredStatus = new ConcurrentHashMap<>();


    /**
     * 计算测试超时时间
     */
    private long getTestTimeoutSeconds(TestConfig config) {
        long baseTimeout = 300; // 默认5分钟

        if (config.getTestMode() == TestMode.DURATION) {
            baseTimeout = config.getDurationSeconds() + 60; // 测试时间 + 1分钟缓冲
        } else if (config.getTestMode() == TestMode.FIXED_COUNT) {
            // 根据请求数量估算时间
            long estimatedSeconds = config.getRequestCount() / 100; // 假设100 QPS
            baseTimeout = Math.max(estimatedSeconds + 60, baseTimeout);
        }

        return Math.min(baseTimeout, 1800); // 最多30分钟
    }

    /**
     * 获取测试状态
     */
    public TestSession getTestSession(String testId) {
        return testSessions.get(testId);
    }

    /**
     * 获取所有测试会话
     */
    public Map<String, TestSession> getAllTestSessions() {
        return new ConcurrentHashMap<>(testSessions);
    }

    /**
     * 清理旧测试会话
     */
    public void cleanupOldSessions(int maxAgeHours) {
        long cutoffTime = System.currentTimeMillis() - (maxAgeHours * 3600000L);

    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        scheduler.shutdownNow();
        testSessions.clear();
        consumerReadyStatus.clear();
        providerRegisteredStatus.clear();
        logger.info("测试管理器已关闭");
    }
}