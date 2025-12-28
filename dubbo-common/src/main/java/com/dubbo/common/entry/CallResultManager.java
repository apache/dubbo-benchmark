package com.dubbo.common.entry;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CallResultManager {
    private static final CallResultManager INSTANCE = new CallResultManager();
    private CallResultManager() {}

    public static CallResultManager getInstance() {
        return INSTANCE;
    }
    private final Map<String, ProvideTestResult> callRecords = new ConcurrentHashMap<>();
    private final ThreadLocal<String> currentCallId = new ThreadLocal<>();

    public String beginCall(String serviceName, ProvideResult provideResult) {
        String callId = "call-" + System.currentTimeMillis() + "-" +
                       Thread.currentThread().getId() + "-" +
                       UUID.randomUUID().toString().substring(0, 8);
        ProvideTestResult provideTestResult = callRecords.get(serviceName);
        if (provideTestResult == null) {
            provideTestResult = new ProvideTestResult();
            provideTestResult.setCount(0);
        }
        provideTestResult.getProvideResultList().add(provideResult);
        provideTestResult.setCount(provideTestResult.getCount() + 1);
        callRecords.put(serviceName, provideTestResult);
        currentCallId.set(callId);
        return callId;
    }

    public String getCurrentCallId() {
        return currentCallId.get();
    }

    public Map<String, ProvideTestResult> getAllResults() {
        return callRecords;
    }

    public void clear() {
        callRecords.clear();
        currentCallId.remove();
    }
}