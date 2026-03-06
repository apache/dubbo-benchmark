package org.apache.dubbo.common.entry;

import org.apache.dubbo.common.conf.ClientType;
import org.apache.dubbo.common.conf.MessageType;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

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
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}