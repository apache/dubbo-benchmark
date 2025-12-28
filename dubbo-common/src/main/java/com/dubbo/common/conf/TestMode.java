package com.dubbo.common.conf;

public enum TestMode {
    /**
     * 固定次数模式：发送指定数量的请求
     * 示例：发送1000次请求
     */
    FIXED_COUNT,
    /**
     * 持续时间模式：在指定时间内持续发送请求
     * 示例：持续发送请求60秒
     */
    DURATION,
    }