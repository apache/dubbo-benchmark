package com.dubbo.common.entry;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Data
public class TestSession {
        private final String testId;
        private final TestConfig config;
        private final Map<String, ConsumerTestResult> consumerResults = new ConcurrentHashMap<>();
        private final long startTime;

        public TestSession(String testId, TestConfig config) {
            this.testId = testId;
            this.config = config;
            this.startTime = System.currentTimeMillis();
        }
    }