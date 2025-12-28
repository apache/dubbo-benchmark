package com.dubbo.common.entry;

import java.io.Serializable;

public class ProviderStatus implements Serializable {
    private String providerId;
    private String host;
    private Integer port;
    private Long startTime;
    private Integer callCount;
    private String status;
}
