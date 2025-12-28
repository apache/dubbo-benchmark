package com.dubbo.common.entry;

import lombok.Data;

import java.util.Date;

/**
 * 统计provide的信息
 */
@Data
public class ProvideResult {
    private String serviceName;
    private String agentHost;
    private String methodName;
    private Date startTime;
    private Date endTime;
    private boolean success;
}
