package org.apache.dubbo.agent.handler;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.apache.dubbo.common.entry.ConsumerTestResult;
import org.apache.dubbo.agent.trendGenerator.ConsumerGeneratorHtml;
import org.apache.dubbo.agent.trendGenerator.ProduceGeneratorHtml;
import org.apache.dubbo.common.conf.ClientType;
import org.apache.dubbo.common.conf.MessageType;
import org.apache.dubbo.common.entry.ClientSession;
import org.apache.dubbo.common.entry.Message;
import org.apache.dubbo.common.entry.TestConfig;
import org.apache.dubbo.common.netty.NettyServer;
import org.apache.dubbo.common.netty.protocol.RegisterMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@ChannelHandler.Sharable
public class AgentNettyHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(AgentNettyHandler.class);
    private static final String RESULT_DIR = "result";
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
            try{
            ClientSession clientSession = nettyServer.getAllSessions().get(msg.getClientType() +msg.getClientId());
            if (clientSession == null) {
                ClientSession session = new ClientSession(registerMessage.getClientId(), ctx.channel(), registerMessage.getClientType());
                nettyServer.getAllSessions().put( msg.getClientType() + "-"+ msg.getClientId(), session);
                Message message = new Message();
                if (msg.getClientType() == ClientType.CONSUMER) {
                    message.setType(MessageType.CONTROL);
                } else {
                    message.setType(MessageType.ACK);
                }
                TestConfig testConfig1 = new TestConfig();
                testConfig1.setTestMode(testConfig.getTestMode());
                testConfig1.setProtocol(testConfig.getProtocol());
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
                    String file_path = "consumer-"+testConfig.getSerialization()+"-" + testResult.getConsumerId() + ".txt";
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
                String file_path = "provide-"+testConfig.getSerialization()+"-" + msg.getClientId() + ".txt";
                appendProvideTestResultToFile(msg.getData(), file_path);
                nettyServer.getAllSessions().remove(msg.getClientType() + "-" + msg.getClientId());
                String file_html_path = "provide-"+testConfig.getSerialization()+"-" + msg.getClientId();
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
    public static void appendTestResultToFile(ConsumerTestResult testResult, String fileName) throws IOException {
        if (testResult == null || fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        String filePath = Paths.get(RESULT_DIR, fileName).toString();
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
    public static void appendProvideTestResultToFile(String testResult, String fileName) throws IOException {
        if (testResult == null || fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        String filePath = Paths.get(RESULT_DIR, fileName).toString();
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        Files.write(
                file.toPath(),
                testResult.getBytes(),
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