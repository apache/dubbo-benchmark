package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

// 注册消息
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterMessage extends Message {
    private TestConfig testConfig;
    private String version = "1.0";
    private Map<String, Object> metadata;
    
    public RegisterMessage() {
        super();
        setType(MessageType.REGISTER);
    }
}