package com.dubbo.common.aop;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class DubboInvokeStatData {

    private final AtomicInteger totalCalls = new AtomicInteger(0);
    private final AtomicInteger successfulCalls = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();

    private final List<Long> responseTimes;
    private final int maxResponseTimeRecords;

    private final Queue<String> recentErrors;
    private final int maxErrorRecords = 100;

    private final Map<String, MethodStat> methodStats = new HashMap<>();
    
    public DubboInvokeStatData(int maxRecords) {
        this.maxResponseTimeRecords = maxRecords;
        this.responseTimes = Collections.synchronizedList(new ArrayList<>());
        this.recentErrors = new ArrayDeque<>(maxErrorRecords);
    }

    public void recordCall(String methodName, long responseTime, boolean success, String error) {
        totalCalls.incrementAndGet();
        
        if (success) {
            successfulCalls.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            recordResponseTime(responseTime);
            MethodStat methodStat = methodStats.computeIfAbsent(methodName, 
                k -> new MethodStat(maxResponseTimeRecords));
            methodStat.recordSuccess(responseTime);
        } else {
            if (error != null) {
                recordError(methodName + " - " + error);
            }

            MethodStat methodStat = methodStats.computeIfAbsent(methodName, 
                k -> new MethodStat(maxResponseTimeRecords));
            methodStat.recordFailure();
        }
    }
    
    private void recordResponseTime(long responseTime) {
        synchronized (responseTimes) {
            if (responseTimes.size() >= maxResponseTimeRecords) {
                responseTimes.remove(0);
            }
            responseTimes.add(responseTime);
        }
    }
    
    private void recordError(String error) {
        synchronized (recentErrors) {
            if (recentErrors.size() >= maxErrorRecords) {
                recentErrors.poll();
            }
            recentErrors.offer(error + " at " + new Date());
        }
    }

    public int getTotalCalls() { return totalCalls.get(); }
    public int getSuccessfulCalls() { return successfulCalls.get(); }
    public double getSuccessRate() {
        return totalCalls.get() == 0 ? 100.0 : 
            successfulCalls.get() * 100.0 / totalCalls.get();
    }
    public double getAverageResponseTime() {
        return successfulCalls.get() == 0 ? 0 : 
            totalResponseTime.get() / (double) successfulCalls.get();
    }
    public double getCurrentQps() {
        long runningSeconds = (System.currentTimeMillis() - startTime) / 1000;
        return runningSeconds > 0 ? totalCalls.get() / (double) runningSeconds : 0;
    }
    
    public List<Long> getResponseTimes() {
        synchronized (responseTimes) {
            return new ArrayList<>(responseTimes);
        }
    }
    
    public List<String> getRecentErrors() {
        synchronized (recentErrors) {
            return new ArrayList<>(recentErrors);
        }
    }
    
    public Map<String, MethodStat> getMethodStats() {
        return new HashMap<>(methodStats);
    }
    
    public void reset() {
        totalCalls.set(0);
        successfulCalls.set(0);
        totalResponseTime.set(0);
        synchronized (responseTimes) {
            responseTimes.clear();
        }
        synchronized (recentErrors) {
            recentErrors.clear();
        }
        methodStats.clear();
    }

    public static class MethodStat {
        private final AtomicInteger calls = new AtomicInteger(0);
        private final AtomicInteger successes = new AtomicInteger(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private final List<Long> responseTimes;
        
        public MethodStat(int maxRecords) {
            this.responseTimes = Collections.synchronizedList(new ArrayList<>());
        }
        
        public void recordSuccess(long responseTime) {
            calls.incrementAndGet();
            successes.incrementAndGet();
            totalTime.addAndGet(responseTime);
            
            synchronized (responseTimes) {
                if (responseTimes.size() >= 1000) {
                    responseTimes.remove(0);
                }
                responseTimes.add(responseTime);
            }
        }
        
        public void recordFailure() {
            calls.incrementAndGet();
        }
        public int getTotalCalls() { return calls.get(); }
        public int getSuccessCalls() { return successes.get(); }
        public double getSuccessRate() {
            return calls.get() == 0 ? 100.0 : successes.get() * 100.0 / calls.get();
        }
        public double getAverageTime() {
            return successes.get() == 0 ? 0 : totalTime.get() / (double) successes.get();
        }
        public List<Long> getResponseTimes() {
            synchronized (responseTimes) {
                return new ArrayList<>(responseTimes);
            }
        }
    }
}