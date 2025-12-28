package com.dubbo.common.aop;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dubbo调用统计管理器
 */
@Component
public class DubboStatManager {

    private static final DubboStatManager INSTANCE = new DubboStatManager();
    private DubboStatManager() {}

    public static DubboStatManager getInstance() {
        return INSTANCE;
    }
    // 按统计名称存储统计数据
    private final Map<String, List<DubboInvokeStatData>> statsMap = new ConcurrentHashMap<>();
    
    // 默认统计实例
    private final DubboInvokeStatData defaultStat = new DubboInvokeStatData(10000);
    
    /**
     * 获取统计实例
     */
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
    /**
     * 获取默认统计实例
     */
    public DubboInvokeStatData getDefaultStat() {
        return defaultStat;
    }
    
    /**
     * 获取所有统计
     */
    public ConcurrentHashMap<String, List<DubboInvokeStatData>> getAllStats() {
        return new ConcurrentHashMap<>(statsMap);
    }
    
    /**
     * 重置指定统计
     */
    public void resetStat(String name) {
        List<DubboInvokeStatData> stat = statsMap.get(name);
        if (stat != null) {
            stat.clear();
        }
    }
    
    /**
     * 重置所有统计
     */
    public void resetAll() {
        defaultStat.reset();
        statsMap.values().forEach(List<DubboInvokeStatData>::clear);
        statsMap.clear();
    }
}