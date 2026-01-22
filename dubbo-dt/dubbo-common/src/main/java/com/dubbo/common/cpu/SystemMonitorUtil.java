package com.dubbo.common.cpu;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SystemMonitorUtil {
    private static AtomicInteger count = new AtomicInteger(0);
    public static Date startTime = new Date();
    public static Date endTime = new Date();
    public static Map<Integer, Integer> CPU_USAGE = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> MEMORY_USAGE = new HashMap<Integer, Integer>();
    private static final OperatingSystemMXBean OS_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean MEMORY_MX_BEAN = ManagementFactory.getMemoryMXBean();
    private static final int CPU_CORES = OS_MX_BEAN.getAvailableProcessors();
    private static final long CPU_SAMPLE_INTERVAL = 500;
    private static Thread thread = new Thread();

    public static void run (){
        startTime = new Date(System.currentTimeMillis());
        thread = new Thread(()->{
            while (true){
                try {
                    count.getAndIncrement();
                    getProcessCpuUsage();
                    Long sum = getJvmHeapTotal();
                    getJvmHeapUsed(sum);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    endTime = new Date(System.currentTimeMillis());
                    log.error(e.getMessage(),e);
                }
            }
        });
        thread.start();
    }

    public static void getProcessCpuUsage() {
        try {
            long[] first = getProcessCpuTimes();
            Thread.sleep(CPU_SAMPLE_INTERVAL);
            long[] second = getProcessCpuTimes();

            long processCpuTime = second[0] - first[0];
            long systemCpuTime = second[1] - first[1];

            if (systemCpuTime <= 0) return ;
            double cpuUsage = (double) processCpuTime / systemCpuTime * CPU_CORES * 100;
            Integer cpu = Math.min(100, Math.max(0, (int) Math.round(cpuUsage)));
            CPU_USAGE.put(count.get(), cpu);
        } catch (Exception e) {
        }
    }

    public static long getJvmHeapTotal() {
        MemoryUsage heap = MEMORY_MX_BEAN.getHeapMemoryUsage();
        return heap.getMax() / 1024 / 1024;
    }

    public static void getJvmHeapUsed(Long memory_max) {
        MemoryUsage heap = MEMORY_MX_BEAN.getHeapMemoryUsage();
        long memory_user = heap.getUsed() / 1024 / 1024;
        int memoryRate = (int) Math.round((double) memory_user / memory_max * 100);
        MEMORY_USAGE.put(count.get(), memoryRate);
    }
    private static long[] getProcessCpuTimes() {
        com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) OS_MX_BEAN;
        return new long[]{sunOsBean.getProcessCpuTime(), System.nanoTime()};
    }

    public static void stop(){
        if (thread != null && thread.isAlive()) {
            try{
                thread.interrupt();
            } catch (Exception e){
                log.error("normal out ");
            }
        }
    }
}