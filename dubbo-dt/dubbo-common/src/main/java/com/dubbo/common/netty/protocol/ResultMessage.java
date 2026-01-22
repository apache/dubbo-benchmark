package com.dubbo.common.netty.protocol;

import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.ConsumerTestResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultMessage extends Message {
    private String testId;
    private ConsumerTestResult testResult;

    public ResultMessage() {
        super();
        setType(MessageType.RESULT);
    }
}