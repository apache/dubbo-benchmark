package com.dubbo.dlt.utils;

public class EnvDurationUtils {
    public static long getDurationInSeconds(String envKey, long defaultMinutes) {
        String minuteStr = System.getenv(envKey);
        long minutes;
        try {
            minutes = Long.parseLong(minuteStr.trim());
        } catch (NumberFormatException e) {
            minutes = defaultMinutes;
        }
        return minutes * 60;
    }
}