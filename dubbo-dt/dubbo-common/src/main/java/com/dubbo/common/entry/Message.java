package com.dubbo.common.entry;

import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import lombok.Data;
import java.io.Serializable;

@Data
public class Message implements Serializable {
    private String messageId;
    private MessageType type;
    private String data;
    private String ClientId;
    private String targetClientId;
    private long timestamp;
    private ClientType clientType;
    
    public Message() {
        this.messageId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
}