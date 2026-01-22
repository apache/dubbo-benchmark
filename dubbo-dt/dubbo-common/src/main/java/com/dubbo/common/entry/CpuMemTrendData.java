package com.dubbo.common.entry;

import lombok.Data;

@Data
public class CpuMemTrendData {
    private String time;
    private Integer cpuUsage;
    private Integer memoryUsage;
}