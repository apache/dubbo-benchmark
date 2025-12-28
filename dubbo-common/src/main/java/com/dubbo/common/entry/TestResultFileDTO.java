package com.dubbo.common.entry;

import lombok.Data;

import java.util.Map;

@Data
public class TestResultFileDTO {
    private String testId;
    private Long startTime; // 时间戳，可按需转成格式化字符串
    private Long endTime;
    private Integer totalRequests;
    private Integer successfulRequests;
    private Integer failedRequests;
    private Map<String, Integer> providerDistribution; // 每个provider的调用次数
}