package com.dubbo.common.aop;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DubboStatManager {

    private static final DubboStatManager INSTANCE = new DubboStatManager();
    private DubboStatManager() {}

    public static DubboStatManager getInstance() {
        return INSTANCE;
    }
    private final Map<String, List<DubboInvokeStatData>> statsMap = new ConcurrentHashMap<>();

    private final DubboInvokeStatData defaultStat = new DubboInvokeStatData(10000);

    public List<DubboInvokeStatData>getStat(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return statsMap.get(name);
    }

    public void recordInvoke(String applicationName, String methodName, long responseTime, boolean success, String error){
        try {
            if (statsMap.get(applicationName) == null) {
                DubboInvokeStatData dubboInvokeStatData = new DubboInvokeStatData(10000);
                dubboInvokeStatData.recordCall(applicationName, responseTime, success, error);
                List<DubboInvokeStatData> dubboInvokeStatDataList = new ArrayList<>();
                dubboInvokeStatDataList.add(dubboInvokeStatData);
                statsMap.put(applicationName, dubboInvokeStatDataList);
                return;
            }
            DubboInvokeStatData dubboInvokeStatData = new DubboInvokeStatData(10000);
            dubboInvokeStatData.recordCall(methodName, responseTime, success, error);
            statsMap.get(applicationName).add(dubboInvokeStatData);
            return;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public DubboInvokeStatData getDefaultStat() {
        return defaultStat;
    }

    public ConcurrentHashMap<String, List<DubboInvokeStatData>> getAllStats() {
        return new ConcurrentHashMap<>(statsMap);
    }

    public void resetStat(String name) {
        List<DubboInvokeStatData> stat = statsMap.get(name);
        if (stat != null) {
            stat.clear();
        }
    }

    public void resetAll() {
        defaultStat.reset();
        statsMap.values().forEach(List<DubboInvokeStatData>::clear);
        statsMap.clear();
    }
}