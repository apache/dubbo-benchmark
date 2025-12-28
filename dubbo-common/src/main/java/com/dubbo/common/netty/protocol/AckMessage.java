package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 确认消息
@Data
@EqualsAndHashCode(callSuper = true)
public class AckMessage extends Message {
    private String requestId;
    private boolean success;
    private String message;
    
    public AckMessage() {
        super();
        setType(MessageType.ACK);
    }
}