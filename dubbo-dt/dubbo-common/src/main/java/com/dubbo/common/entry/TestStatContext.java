package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class TestStatContext {
    private String consumerId;

    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger successRequests = new AtomicInteger(0);
    private AtomicInteger failedRequests = new AtomicInteger(0);
    private AtomicLong totalResponseTime = new AtomicLong(0);
    private AtomicInteger currentConcurrent = new AtomicInteger(0);
    private AtomicInteger peakConcurrent = new AtomicInteger(0);

    private Date startTime;
    private Date endTime;

    private Map<String, Integer> produceDistribution = new java.util.concurrent.ConcurrentHashMap<>();
    
    public TestStatContext(String consumerId) {
        this.consumerId = consumerId;
        this.startTime = new Date(System.currentTimeMillis());
    }

    public void recordSuccess(long responseTime) {
        totalRequests.incrementAndGet();
        successRequests.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);
    }

    public void recordFailure() {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
    }

    public void recordProvider(String providerName) {
        if (providerName != null) {
            produceDistribution.merge(providerName, 1, Integer::sum);
        }
    }

    public void increaseConcurrent() {
        int curr = currentConcurrent.incrementAndGet();
        while (true) {
            int peak = peakConcurrent.get();
            if (curr > peak && peakConcurrent.compareAndSet(peak, curr)) {
                break;
            } else {
                break;
            }
        }
    }

    public void decreaseConcurrent() {
        if (currentConcurrent.get() > 0) {
            currentConcurrent.decrementAndGet();
        }
    }

    public void endTest() {
        this.endTime = new Date(System.currentTimeMillis());
    }

    public double getTestDurationSeconds() {
        long durationMs = totalResponseTime.get();
        return durationMs / 1000.00;
    }

    public String getThroughput() {
        double durationSeconds = getTestDurationSeconds();
        int totalReq = totalRequests.get();
        if (durationSeconds <= 0 || totalReq == 0) {
            return 0 + "次";
        }
        double throughput = totalReq / durationSeconds;
        return (int)throughput + "次";
    }

    public String getAvgResponseTime() {
        int successReq = successRequests.get();
        long totalRespTime = totalResponseTime.get();
        if (successReq == 0 || totalRespTime == 0) {
            return 0+ "ms";
        }
        return (totalRespTime * 1.0D / successReq) + "ms";
    }

    public double getSuccessRate() {
        int totalReq = totalRequests.get();
        int successReq = successRequests.get();
        if (totalReq == 0) {
            return 0.0D;
        }
        return (successReq * 100.0D) / totalReq;
    }

    public ConsumerTestResult toTestResult() {
        ConsumerTestResult result = new ConsumerTestResult();
        result.setConsumerId(consumerId);
        result.setStartTime(startTime);
        result.setEndTime(endTime != null ? endTime : new Date(System.currentTimeMillis()));
        result.setTotalRequests(totalRequests.get());
        result.setSuccessfulRequests(successRequests.get());
        result.setFailedRequests(failedRequests.get());
        result.setProviderDistribution(new HashMap<>(produceDistribution));
        result.setThroughput(getThroughput());
        result.setAvgResponseTime(getAvgResponseTime());
        result.setSuccessRate(getSuccessRate());
        result.setTestDurationSeconds(getTestDurationSeconds());
        result.setPeakConcurrent(peakConcurrent.get() + "次");
        return result;
    }
}