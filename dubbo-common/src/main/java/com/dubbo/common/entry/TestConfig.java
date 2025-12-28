package com.dubbo.common.entry;

import com.dubbo.common.conf.TestMode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Map;

@Data
public class TestConfig implements Serializable {
    @Value("${spring.application.name}")
    private String consumerId;
    @Value("test_mode")
    private TestMode testMode;
    @Value("${requestCount}")
    private Integer requestCount;
    @Value("${durationSeconds}")
    private Long durationSeconds;
    private String namespace;
    private String locadbance;
    private Integer qpsLimit;
    private Map<String, Object> extraParams;
}
