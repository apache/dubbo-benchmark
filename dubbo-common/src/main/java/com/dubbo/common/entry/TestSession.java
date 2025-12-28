package com.dubbo.common.entry;

import com.dubbo.common.conf.Status;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 测试会话
 */
@Data
public class TestSession {
        private final String testId;
        private Status status;
        private final TestConfig config;
        private final Map<String, ConsumerTestResult> consumerResults = new ConcurrentHashMap<>();
        private final Map<String, QoPData> providerQoPData = new ConcurrentHashMap<>();
        private final long startTime;

        public TestSession(String testId, TestConfig config) {
            this.testId = testId;
            this.config = config;
            this.startTime = System.currentTimeMillis();
        }
    }