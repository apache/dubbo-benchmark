package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import lombok.Data;

@Data
public class HeartbeatMessage{
    private int load;
    private MessageType messageType;
    private String clientId;
    public HeartbeatMessage() {
        super();
       messageType = MessageType.HEARTBEAT;
    }
}