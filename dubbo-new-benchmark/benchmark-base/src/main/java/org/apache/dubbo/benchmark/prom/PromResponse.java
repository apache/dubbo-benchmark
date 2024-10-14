package org.apache.dubbo.benchmark.prom;

import lombok.Data;

@Data
public class PromResponse {
    private String status;

    private PromDataInfo data;
}
