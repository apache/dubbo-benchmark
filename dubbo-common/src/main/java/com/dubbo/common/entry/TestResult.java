package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class TestResult {
    private List<ConsumerTestResult> consumerTestResults;
    private Date startTime;
    private Long throughput;
    private Date endTime;
}
