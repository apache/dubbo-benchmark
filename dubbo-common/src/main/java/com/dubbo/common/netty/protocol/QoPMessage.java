package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.QoPData;
import lombok.Data;
import lombok.EqualsAndHashCode;

// QOP消息
@Data
@EqualsAndHashCode(callSuper = true)
public class QoPMessage extends Message {
    private QoPData qoPData;
    
    public QoPMessage() {
        super();
        setType(MessageType.QOP);
    }
}