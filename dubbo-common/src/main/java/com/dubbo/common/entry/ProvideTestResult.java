package com.dubbo.common.entry;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ProvideTestResult {
    private List<ProvideResult> provideResultList;
    private Integer count;

    public List<ProvideResult> getProvideResultList() {
        if (provideResultList == null) {
            provideResultList = new ArrayList<>();  // 懒加载
        }
        return provideResultList;
    }

    public void setProvideResultList(List<ProvideResult> provideResultList) {
        this.provideResultList = provideResultList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
