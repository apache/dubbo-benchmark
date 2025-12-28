package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.ControlCommand;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControlMessage extends Message {
    private ControlCommand command;
    
    public ControlMessage() {
        super();
        setType(MessageType.CONTROL);
    }
}