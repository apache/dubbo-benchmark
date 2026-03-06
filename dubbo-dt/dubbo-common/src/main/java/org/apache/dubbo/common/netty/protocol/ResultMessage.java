package org.apache.dubbo.common.netty.protocol;

import org.apache.dubbo.common.conf.MessageType;
import org.apache.dubbo.common.entry.Message;
import org.apache.dubbo.common.entry.ConsumerTestResult;
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