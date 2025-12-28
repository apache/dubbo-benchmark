package com.dubbo.dlt.trendGenerator.entry;
import java.time.LocalDateTime;



public class CallRecord {
        String methodName;
        String startTime;
        boolean success;
        LocalDateTime startTimeDt;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getStartTimeDt() {
        return startTimeDt;
    }

    public void setStartTimeDt(LocalDateTime startTimeDt) {
        this.startTimeDt = startTimeDt;
    }
    private static final java.time.format.DateTimeFormatter TIME_FORMAT = new java.time.format.DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(java.time.temporal.ChronoField.MILLI_OF_SECOND, 0, 3, true)
            .toFormatter();
    public CallRecord(String methodName, String startTime, boolean success) {
            this.methodName = methodName;
            this.startTime = startTime;
            this.success = success;
            this.startTimeDt = LocalDateTime.parse(startTime,  TIME_FORMAT);
        }

    }