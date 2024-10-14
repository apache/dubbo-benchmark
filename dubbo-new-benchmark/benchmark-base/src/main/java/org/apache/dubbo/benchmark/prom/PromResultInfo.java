package org.apache.dubbo.benchmark.prom;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class PromResultInfo implements Serializable {
    /**
     * prometheus指标属性
     */
    private PromMetricInfo metric;

    /**
     * prometheus指标值
     */
    private List<double[]> values;
}
