package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;

@Data
public class ProduceResult {
    private String serviceName;
    private String agentHost;
    private String methodName;
    private Date startTime;
    private Date endTime;
    private boolean success;
}
