package com.dubbo.common.conf;

public enum Status {
            CREATED,      // 已创建
            WAITING,      // 等待Consumer准备
            RUNNING,      // 运行中
            COMPLETED,    // 已完成
            FAILED,       // 失败
            STOPPED,      // 已停止
            TIMEOUT       // 超时
        }