package com.dubbo.common.consumer;

import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.scan.SimpleDubboScanner;
import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class ConsumerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger log = LoggerFactory.getLogger(ConsumerHandler.class);
    private TestConfig testConfig;
    private TestExecutor testExecutor;
    private SimpleDubboScanner  simpleDubboScanner;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {}
    public ConsumerHandler(TestExecutor testExecutor, SimpleDubboScanner simpleDubboScanner, TestConfig testConfig) {
        this.testExecutor = testExecutor;
        this.simpleDubboScanner = simpleDubboScanner;
        this.testConfig = testConfig;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        MessageType type = message.getType();
        if  (type ==  MessageType.CONTROL) {
            TestConfig testConfig1 = JSONObject.parseObject(message.getData(), TestConfig.class);
            testConfig1.setConsumerId(testConfig.getConsumerId());
            simpleDubboScanner.run();
            List<Method> allMethodsByAnnotationValue = simpleDubboScanner.findAllMethodsByAnnotationValue(testConfig1.getNamespace());
            List<ConsumerTestResult> testResultList = testExecutor.executeTest(testConfig1, allMethodsByAnnotationValue);
            Message response = new Message();
            response.setClientId(testConfig.getConsumerId());
            response.setType(MessageType.RESULT);
            response.setClientId(testConfig.getConsumerId());
            response.setClientType(ClientType.CONSUMER);
            response.setData(JSONObject.toJSONString(testResultList));
            channelHandlerContext.writeAndFlush(response);
        }

    }
}
