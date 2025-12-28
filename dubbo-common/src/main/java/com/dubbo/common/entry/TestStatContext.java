package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试统计上下文（线程安全）
 */
@Data
public class TestStatContext {
    // 测试标识
    private String testId;
    private String consumerId;
    
    // 统计计数器
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger successRequests = new AtomicInteger(0);
    private AtomicInteger failedRequests = new AtomicInteger(0);
    private AtomicLong totalResponseTime = new AtomicLong(0);
    private AtomicInteger currentConcurrent = new AtomicInteger(0);
    private AtomicInteger peakConcurrent = new AtomicInteger(0);
    
    // 开始和结束时间
    private Date startTime;
    private Date endTime;
    
    // Provider分布统计
    private Map<String, Integer> providerDistribution = new java.util.concurrent.ConcurrentHashMap<>();
    
    public TestStatContext(String testId, String consumerId) {
        this.testId = testId;
        this.consumerId = consumerId;
        this.startTime = new Date(System.currentTimeMillis());
    }
    
    /**
     * 记录一次成功调用
     */
    public void recordSuccess(long responseTime) {
        totalRequests.incrementAndGet();
        successRequests.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);
    }
    
    /**
     * 记录一次失败调用
     */
    public void recordFailure() {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
    }
    
    /**
     * 记录Provider分布
     */
    public void recordProvider(String providerName) {
        if (providerName != null) {
            providerDistribution.merge(providerName, 1, Integer::sum);
        }
    }
    /**
     * 请求开始时调用 → 并发数+1
     */
    public void increaseConcurrent() {
        int curr = currentConcurrent.incrementAndGet();
        // 实时更新峰值并发数：如果当前并发数 > 历史峰值，就更新峰值
        while (true) {
            int peak = peakConcurrent.get();
            if (curr > peak && peakConcurrent.compareAndSet(peak, curr)) {
                break;
            } else {
                break;
            }
        }
    }

    /**
     * 请求结束时调用 → 并发数-1 【必须在过滤器finally块执行，无论成功失败都要调用】
     */
    public void decreaseConcurrent() {
        if (currentConcurrent.get() > 0) {
            currentConcurrent.decrementAndGet();
        }
    }
    
    /**
     * 结束测试
     */
    public void endTest() {
        this.endTime = new Date(System.currentTimeMillis());
    }

    public double getTestDurationSeconds() {
        long endTimeMs = endTime != null ? endTime.getTime() : System.currentTimeMillis();
        long durationMs = endTimeMs - startTime.getTime();
        // 转成秒，保留小数，避免整除丢失精度
        return durationMs / 1000.00;
    }

    /**
     * 计算【这段时间内的吞吐量】：核心方法 ✅
     * 吞吐量 = 总请求数 / 测试耗时(秒)  单位：请求数/秒
     */
    public String getThroughput() {
        double durationSeconds = getTestDurationSeconds();
        int totalReq = totalRequests.get();
        // 除零保护：测试时长为0时，吞吐量为0，避免算术异常
        if (durationSeconds <= 0 || totalReq == 0) {
            return 0 + "次";
        }
        double throughput = totalReq / durationSeconds;
        return (int)throughput + "次";
    }

    /**
     * 计算【平均响应耗时】：成功请求的平均耗时，单位：毫秒
     */
    public String getAvgResponseTime() {
        int successReq = successRequests.get();
        long totalRespTime = totalResponseTime.get();
        if (successReq == 0 || totalRespTime == 0) {
            return 0+ "s";
        }
        return (totalRespTime * 1.0D / successReq) + "s";
    }

    /**
     * 计算【请求成功率】：百分比（0-100）
     */
    public double getSuccessRate() {
        int totalReq = totalRequests.get();
        int successReq = successRequests.get();
        if (totalReq == 0) {
            return 0.0D;
        }
        return (successReq * 100.0D) / totalReq;
    }
    
    /**
     * 获取测试结果
     */
    public ConsumerTestResult toTestResult() {
        ConsumerTestResult result = new ConsumerTestResult();
        result.setTestId(testId);
        result.setConsumerId(consumerId);
        result.setStartTime(startTime);
        result.setEndTime(endTime != null ? endTime : new Date(System.currentTimeMillis()));
        result.setTotalRequests(totalRequests.get());
        result.setSuccessfulRequests(successRequests.get());
        result.setFailedRequests(failedRequests.get());
        result.setProviderDistribution(new HashMap<>(providerDistribution));
        result.setThroughput(getThroughput());
        result.setAvgResponseTime(getAvgResponseTime());
        result.setSuccessRate(getSuccessRate());
        result.setTestDurationSeconds(getTestDurationSeconds());
        result.setPeakConcurrent(peakConcurrent.get() + "次");
        return result;
    }
}