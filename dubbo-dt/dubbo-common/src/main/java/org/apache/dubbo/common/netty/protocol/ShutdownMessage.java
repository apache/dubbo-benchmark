package org.apache.dubbo.common.netty.protocol;

import org.apache.dubbo.common.conf.MessageType;
import org.apache.dubbo.common.entry.Message;
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