package com.dubbo.common.entry;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
@Data
public class ConsumerTestResult implements Serializable {
    private String consumerId;
    private Date startTime;
    private Date endTime;
    private String throughput;
    private String avgResponseTime;
    private double successRate;
    private double testDurationSeconds;
    private Long totalResponseTime;
    private Integer totalRequests;
    private String peakConcurrent;
    private Integer successfulRequests;
    private Integer failedRequests;
    private Map<String, Integer> providerDistribution;
    private Map<String, Object> performanceMetrics;
    private String resultJson;
}
