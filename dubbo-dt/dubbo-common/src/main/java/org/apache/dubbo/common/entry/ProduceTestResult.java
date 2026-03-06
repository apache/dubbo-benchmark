package org.apache.dubbo.common.entry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ProduceTestResult {
    private List<ProduceResult> produceResultList;
    private final AtomicInteger count = new AtomicInteger(0);
    public ProduceTestResult() {
        this.produceResultList = new CopyOnWriteArrayList<>();
    }
    public List<ProduceResult> getProvideResultList() {
        if (produceResultList == null) {
            produceResultList = new CopyOnWriteArrayList<>();
        }
        return produceResultList;
    }

    public List<ProduceResult> getProduceResultList() {
        return produceResultList;
    }


    public void addProduceResult(ProduceResult result) {
        produceResultList.add(result);
        count.incrementAndGet();
    }
    public Integer getCount() {
        return count.get();
    }

    public void incrementCount() {
        this.count.incrementAndGet();
    }
}
