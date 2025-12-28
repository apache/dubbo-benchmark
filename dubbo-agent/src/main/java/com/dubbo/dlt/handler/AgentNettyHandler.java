package com.dubbo.dlt.handler;

import com.dubbo.common.entry.ConsumerTestResult;
import com.dubbo.dlt.trendGenerator.ProviderCallTrendGenerator;
import com.dubbo.dlt.utils.EnvDurationUtils;
import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.conf.TestMode;
import com.dubbo.common.entry.ClientSession;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.TestConfig;
import com.dubbo.common.netty.NettyServer;
import com.dubbo.common.netty.protocol.HeartbeatMessage;
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
import java.util.regex.Pattern;

@ChannelHandler.Sharable
public class AgentNettyHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(AgentNettyHandler.class);
    private  NettyServer nettyServer;
    String spacename = System.getenv("Agentname");
    Long durationSeconds = EnvDurationUtils.getDurationInSeconds("durationSeconds" , 1000);
    Integer requestCount = Integer.valueOf(System.getenv("requestCount"));
    public AgentNettyHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println(msg);
        MessageType type = msg.getType();
        System.out.println(type);
        if (type == MessageType.HEARTBEAT) {
            String heart = msg.getData();
            HeartbeatMessage heartbeatMessage = JSONObject.parseObject(heart, HeartbeatMessage.class);
            System.out.println("解析成功！HeartbeatMessage");
            ClientSession clientSession = nettyServer.getAllSessions().get(msg.getClientType() + "-" + msg.getClientId());
            if (clientSession.isHeartbeatTimeout()) {
                logger.info("消费者:%s 掉线", heartbeatMessage.getClientId());
                clientSession.getChannel().close();
            }
            clientSession.updateHeartbeat();
            logger.info("更新成功");
        } else if (type == MessageType.REGISTER) {
            logger.info("开始测试 ");
            RegisterMessage registerMessage = JSONObject.parseObject(msg.getData(), RegisterMessage.class);
            System.out.println(registerMessage);
            System.out.println(msg.getClientId());
            try{
            ClientSession clientSession = nettyServer.getAllSessions().get(msg.getClientType() +msg.getClientId());
            if (clientSession == null) {
                ClientSession session = new ClientSession(registerMessage.getClientId(), ctx.channel(), registerMessage.getClientType());
                nettyServer.getAllSessions().put( msg.getClientType() + "-"+ msg.getClientId(), session);
                if (msg.getClientType() == ClientType.CONSUMER){
                    logger.info("发送测试成功");
                    Message message = new Message();
                    message.setType(MessageType.CONTROL);
                    TestConfig testConfig = new TestConfig();
                    testConfig.setTestMode(TestMode.FIXED_COUNT);
                    testConfig.setConsumerId(msg.getClientId());
                    testConfig.setNamespace(spacename);
                    logger.info(spacename);
                    testConfig.setDurationSeconds(durationSeconds);
                    logger.info(String.valueOf(durationSeconds));
                    testConfig.setRequestCount(requestCount);
                    logger.info(String.valueOf(requestCount));
                    message.setData(JSONObject.toJSONString(testConfig));
                    session.getChannel().writeAndFlush(message);
                    }
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            System.out.println("Test的结果");
        } else if (type == MessageType.RESULT) {
            logger.info("接收到结果");
            if (msg.getClientType() == ClientType.CONSUMER) {
                List<ConsumerTestResult> testResultList = JSONObject.parseObject(
                        msg.getData(),
                        new TypeReference<List<ConsumerTestResult>>() {
                        }
                );
                for (ConsumerTestResult testResult : testResultList) {
                    String file_path = "consumer_result" + testResult.getConsumerId() + ".txt";
                    appendTestResultToFile(testResult, file_path);
                }
                if (msg.getClientType() == ClientType.CONSUMER) {
                    boolean b = handleMessage(msg);
                    if (b) {
                        send_Message_toProvide();
                    }
                }
            } else if (msg.getClientType() == ClientType.PROVIDER) {
                String file_path = "provide_result" + msg.getClientId() + ".txt";
                appendProvideTestResultToFile(msg.getData(), file_path);
                nettyServer.getAllSessions().remove(msg.getClientType() + "-" + msg.getClientId());
                String file_html_path = "provide_result" + msg.getClientId() + ".html";
                ProviderCallTrendGenerator.generateCallTrendHtml(msg.getData() , file_html_path);
            }
        }else if (type == MessageType.HEARTBEAT){}
        else {
            logger.info("解析失败");
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
            throw new IllegalArgumentException("参数不合法");
        }
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        String jsonStr = JSONObject.toJSONString(testResult);
        jsonStr = "\n---分割线---\n" + jsonStr;

        Files.write(
                file.toPath(),
                jsonStr.getBytes(),
                java.nio.file.StandardOpenOption.CREATE, // 文件不存在则创建
                java.nio.file.StandardOpenOption.APPEND  // 追加模式
        );

        logger.info("TestResult已追加写入文件：" + file.getAbsolutePath());
    }
    public static void appendProvideTestResultToFile(String testResult, String filePath) throws IOException {
        if (testResult == null || filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("参数不合法");
        }
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        String cleanResult = testResult
                // 替换 转义形式的 \n （重点！解决你的核心问题）
                .replaceAll("\\\\n", "")
                // 替换 真实换行符
                .replaceAll("\\n", "")
                // 替换 转义形式的 \r
                .replaceAll("\\\\r", "")
                // 替换 真实回车符
                .replaceAll("\\r", "")
                // 替换 转义形式的 \t 制表符
                .replaceAll("\\\\t", "")
                .replaceAll(Pattern.quote("\\"), "")  // 去除所有转义斜杠 \
                .replaceAll("^\"|\"$", "")
                // 替换 真实制表符
                .replaceAll("\\t", "");
        String jsonStr = JSONObject.toJSONString(testResult);
        jsonStr = "\n---分割线---\n" + jsonStr;

        Files.write(
                file.toPath(),
                jsonStr.getBytes(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
        );

        logger.info("TestResult已追加写入文件：" + file.getAbsolutePath());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端连接建立: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx); // 执行父类逻辑
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端连接断开: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    // 4. 异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端通信异常: {}", ctx.channel().remoteAddress(), cause);
        ctx.close(); // 异常时关闭连接
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