package com.dubbo.common.entry;

import com.dubbo.common.conf.TestMode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Map;

@Data
public class TestConfig implements Serializable {
    private String consumerId;
    private TestMode testMode;
    private Integer requestCount;
    private Long durationSeconds;
    private String namespace;
    private String locadbance;
    private String serialization;
    private Integer qpsLimit;
    private Map<String, Object> extraParams;
}
