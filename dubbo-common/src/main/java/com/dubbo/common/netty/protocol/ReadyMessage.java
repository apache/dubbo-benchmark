package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReadyMessage extends Message {
    private String testId;
    private TestConfig testConfig;
    
    public ReadyMessage() {
        super();
        setType(MessageType.READY);
    }
}