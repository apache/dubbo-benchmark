package org.apache.dubbo.common.entry;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CallResultManager {
    private static final CallResultManager INSTANCE = new CallResultManager();
    private CallResultManager() {}

    public static CallResultManager getInstance() {
        return INSTANCE;
    }
    private final ConcurrentHashMap<String, ProduceTestResult> callRecords = new ConcurrentHashMap<>();
    private final ThreadLocal<String> currentCallId = new ThreadLocal<>();

    public String beginCall(String serviceName, ProduceResult produceResult) {
        String callId = "call-" + System.currentTimeMillis() + "-" +
                       Thread.currentThread().getId() + "-" +
                       UUID.randomUUID().toString().substring(0, 8);
        ProduceTestResult produceTestResult = callRecords.computeIfAbsent(
                serviceName,
                k -> new ProduceTestResult()
        );
        produceTestResult.addProduceResult(produceResult);
        currentCallId.set(callId);
        return callId;
    }

    public String getCurrentCallId() {
        return currentCallId.get();
    }

    public Map<String, ProduceTestResult> getAllResults() {
        return callRecords;
    }

    public void clear() {
        callRecords.clear();
        currentCallId.remove();
    }
}