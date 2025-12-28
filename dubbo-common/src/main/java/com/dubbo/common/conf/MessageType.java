package com.dubbo.common.conf;

public enum MessageType {
    REGISTER,      // 注册
    HEARTBEAT,     // 心跳
    READY,         // 准备好
    CONTROL,       // 控制指令
    RESULT,        // 测试结果
    QOP,          // QOP数据
    SHUTDOWN,      // 下线
    ACK,          // 确认
}