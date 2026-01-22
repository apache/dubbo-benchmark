package com.dubbo.dlt.trendGenerator.entry;

import java.util.Map;

public class TrendData {
    private String time;
    private int totalCount;
    private double successRate;
    private Map<String, Integer> methodCountMap;

    public String getTime() { return time; }
    public int getTotalCount() { return totalCount; }
    public double getSuccessRate() { return successRate; }
    public Map<String, Integer> getMethodCountMap() { return methodCountMap; }

    public void setTime(String time) { this.time = time; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public void setMethodCountMap(Map<String, Integer> methodCountMap) { this.methodCountMap = methodCountMap; }
}