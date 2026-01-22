package com.dubbo.common.entry;

import java.util.ArrayList;
import java.util.List;

public class ProduceTestResult {
    private List<ProduceResult> produceResultList;
    private Integer count;

    public List<ProduceResult> getProvideResultList() {
        if (produceResultList == null) {
            produceResultList = new ArrayList<>();
        }
        return produceResultList;
    }

    public void setProvideResultList(List<ProduceResult> produceResultList) {
        this.produceResultList = produceResultList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
