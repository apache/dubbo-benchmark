package com.dubbo.dlt.trendGenerator.entry;

import java.util.Map;

public class TrendData {
    private String time;                // 聚合维度：秒/分钟
    private int totalCount;             // 当前时间维度的总调用量
    private double successRate;         // 当前时间维度的成功率
    private Map<String, Integer> methodCountMap; // 动态存储【方法名-调用量】，支持任意方法

    // 必须加getter方法，FastJSON序列化必备，解决空对象问题
    public String getTime() { return time; }
    public int getTotalCount() { return totalCount; }
    public double getSuccessRate() { return successRate; }
    public Map<String, Integer> getMethodCountMap() { return methodCountMap; }

    // 给setter方法，方便赋值
    public void setTime(String time) { this.time = time; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public void setMethodCountMap(Map<String, Integer> methodCountMap) { this.methodCountMap = methodCountMap; }
}