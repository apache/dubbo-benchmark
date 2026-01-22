package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShutdownMessage extends Message {
    private String reason;
    
    public ShutdownMessage() {
        super();
        setType(MessageType.SHUTDOWN);
    }
}