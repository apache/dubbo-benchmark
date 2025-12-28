package com.dubbo.common.netty.lister;

import com.dubbo.common.entry.Message;
import com.dubbo.common.netty.protocol.ControlMessage;
import com.dubbo.common.netty.protocol.ShutdownMessage;

/**
     * 消息监听器接口
     */
    public interface MessageListener {
        void onMessage(Message message);
        void onControlMessage(ControlMessage message);
        void onShutdown(ShutdownMessage message);
    }