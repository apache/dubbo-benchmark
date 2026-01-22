package com.dubbo.dlt.manager;


import com.dubbo.common.entry.TestSession;
import com.dubbo.dlt.NettyServeragentService;
import com.dubbo.common.conf.TestMode;
import com.dubbo.common.entry.TestConfig;
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


    private long getTestTimeoutSeconds(TestConfig config) {
        long baseTimeout = 300;

        if (config.getTestMode() == TestMode.DURATION) {
            baseTimeout = config.getDurationSeconds() + 60;
        } else if (config.getTestMode() == TestMode.FIXED_COUNT) {
            long estimatedSeconds = config.getRequestCount() / 100;
            baseTimeout = Math.max(estimatedSeconds + 60, baseTimeout);
        }

        return Math.min(baseTimeout, 1800);
    }

    public TestSession getTestSession(String testId) {
        return testSessions.get(testId);
    }

    public Map<String, TestSession> getAllTestSessions() {
        return new ConcurrentHashMap<>(testSessions);
    }

    public void cleanupOldSessions(int maxAgeHours) {
        long cutoffTime = System.currentTimeMillis() - (maxAgeHours * 3600000L);

    }

    public void shutdown() {
        scheduler.shutdownNow();
        testSessions.clear();
        consumerReadyStatus.clear();
        providerRegisteredStatus.clear();
    }
}