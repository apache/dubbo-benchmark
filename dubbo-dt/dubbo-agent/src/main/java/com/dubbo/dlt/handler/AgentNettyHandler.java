package com.dubbo.dlt.handler;

import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.dlt.trendGenerator.ConsumerGeneratorHtml;
import com.dubbo.dlt.trendGenerator.ProduceGeneratorHtml;
import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.entry.ClientSession;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.NettyServer;
import com.dubbo.common.netty.protocol.RegisterMessage;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@ChannelHandler.Sharable
public class AgentNettyHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(AgentNettyHandler.class);
    private  NettyServer nettyServer;
    private  TestConfig testConfig;
    public AgentNettyHandler(NettyServer nettyServer ,TestConfig testconfig) {
        this.nettyServer = nettyServer;
        this.testConfig = testconfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageType type = msg.getType();
        if (type == MessageType.HEARTBEAT) {
            ClientSession clientSession = nettyServer.getAllSessions().get(msg.getClientType() + "-" + msg.getClientId());
            if (clientSession.isHeartbeatTimeout()) {
                clientSession.getChannel().close();
            }
            clientSession.updateHeartbeat();
        } else if (type == MessageType.REGISTER) {
            RegisterMessage registerMessage = JSONObject.parseObject(msg.getData(), RegisterMessage.class);
            System.out.println(registerMessage);
            System.out.println(msg.getClientId());
            try{
            ClientSession clientSession = nettyServer.getAllSessions().get(msg.getClientType() +msg.getClientId());
            if (clientSession == null) {
                ClientSession session = new ClientSession(registerMessage.getClientId(), ctx.channel(), registerMessage.getClientType());
                nettyServer.getAllSessions().put( msg.getClientType() + "-"+ msg.getClientId(), session);
                if (msg.getClientType() == ClientType.CONSUMER){
                    Message message = new Message();
                    message.setType(MessageType.CONTROL);
                    TestConfig testConfig1 = new TestConfig();
                    testConfig1.setTestMode(testConfig1.getTestMode());
                    testConfig1.setNamespace(testConfig.getNamespace());
                    testConfig1.setDurationSeconds(testConfig.getDurationSeconds());
                    testConfig1.setRequestCount(testConfig.getRequestCount());
                    testConfig1.setLocadbance(testConfig.getLocadbance());
                    testConfig1.setTestMode(testConfig.getTestMode());
                    testConfig1.setSerialization(testConfig.getSerialization());
                    message.setData(JSONObject.toJSONString(testConfig1));
                    session.getChannel().writeAndFlush(message);
                    }
                }
            }
            catch (Exception e){
                logger.error(e.getMessage());
            }
        } else if (type == MessageType.RESULT) {
            if (msg.getClientType() == ClientType.CONSUMER) {
                List<ConsumerTestResult> testResultList = JSONObject.parseObject(
                        msg.getData(),
                        new TypeReference<List<ConsumerTestResult>>() {
                        }
                );
                for (ConsumerTestResult testResult : testResultList) {
                    String file_path = "consumer-result-" + testResult.getConsumerId() + ".txt";
                    appendTestResultToFile(testResult, file_path);
                }
                ConsumerGeneratorHtml.writeConsumerHtml(msg.getData(), testConfig.getTestMode().toString().toLowerCase()+ "-"+msg.getClientType().toString().toLowerCase()+'-' + msg.getClientId());
                if (msg.getClientType() == ClientType.CONSUMER) {
                    boolean b = handleMessage(msg);
                    if (b) {
                        send_Message_toProvide();
                    }
                }
            } else if (msg.getClientType() == ClientType.PROVIDER) {
                if (msg.getData().isEmpty() || msg.getData().equals("{}")) {
                    return;
                }
                String file_path = "provide-result-" + msg.getClientId() + ".txt";
                appendProvideTestResultToFile(msg.getData(), file_path);
                nettyServer.getAllSessions().remove(msg.getClientType() + "-" + msg.getClientId());
                String file_html_path = "provide-ressult-" + msg.getClientId();
                ProduceGeneratorHtml.generateCallTrendHtml(msg.getData() , file_html_path);
            }
        }else if (type == MessageType.HEARTBEAT){}
        else {
        }

    }

    private void send_Message_toProvide() {
        for (ClientSession value : nettyServer.getAllSessions().values()) {
            Message message = new Message();
            message.setType(MessageType.CONTROL);
            if (value.getChannel().isActive()) {
                value.getChannel().writeAndFlush(message);
            }
        }

    }
    public static void appendTestResultToFile(ConsumerTestResult testResult, String filePath) throws IOException {
        if (testResult == null || filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        String jsonStr = JSONObject.toJSONString(testResult);

        Files.write(
                file.toPath(),
                jsonStr.getBytes(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
        );

    }
    public static void appendProvideTestResultToFile(String testResult, String filePath) throws IOException {
        if (testResult == null || filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        String jsonStr = JSONObject.toJSONString(testResult);

        Files.write(
                file.toPath(),
                jsonStr.getBytes(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
        );
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
    private boolean handleMessage(Message message){
        ClientSession clientSession = nettyServer.getAllSessions().get(message.getClientType() + "-" +message.getClientId());
        if (clientSession != null) {
            clientSession.getChannel().close();
        }else {
            return false;
        }
        nettyServer.getAllSessions().remove(message.getClientType() + "-" + message.getClientId());
        return haveConsumers();
    }

    private boolean haveConsumers() {
        for (String s : nettyServer.getAllSessions().keySet()) {
            if (s.startsWith("CONSUMER")){
                return false;
            }
        }
        return true;
    }

}