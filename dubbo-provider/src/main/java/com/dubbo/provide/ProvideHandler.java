package com.dubbo.provide;

import com.alibaba.fastjson2.JSON;
import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.CallResultManager;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.ProvideTestResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
public class ProvideHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger log = LoggerFactory.getLogger(ProvideHandler.class);
    private final CallResultManager callResultManager = CallResultManager.getInstance();
    private String providerId;
    ProvideHandler(String providerId) {
        this.providerId = providerId;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        log.info("开始测试");
        if (message.getType() == MessageType.CONTROL) {
            Map<String, ProvideTestResult> allResults = callResultManager.getAllResults();
            Message msg = new Message();
            msg.setType(MessageType.RESULT);
            msg.setClientType(ClientType.PROVIDER);
            msg.setClientId(providerId);
            msg.setData(JSON.toJSONString(allResults));
            channelHandlerContext.writeAndFlush(msg);
        }
    }
}
