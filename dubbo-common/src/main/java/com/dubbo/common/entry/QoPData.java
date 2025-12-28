package com.dubbo.common.entry;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
@Data
public class QoPData implements Serializable {
    private String providerId;
    private Long timestamp;
    private Double avgResponseTime;     // 平均响应时间
    private Double p95ResponseTime;     // P95响应时间
    private Double p99ResponseTime;     // P99响应时间
    private Double successRate;         // 成功率
    private Integer qps;                // 每秒查询数
    private Integer concurrentCalls;    // 并发调用数
    private Map<String, Object> customMetrics; // 自定义指标
}
