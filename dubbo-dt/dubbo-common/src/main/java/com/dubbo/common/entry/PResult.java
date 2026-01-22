package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;
import java.util.Map;
@Data
public class PResult {
    private Map<String, ProduceTestResult>  allResults;
    private Map<Integer, Integer>  memoryUsage;
    private Map<Integer, Integer>  cpuUsage;
    private Date cpuStartTime;
    private Date cpuEndTime;
}
