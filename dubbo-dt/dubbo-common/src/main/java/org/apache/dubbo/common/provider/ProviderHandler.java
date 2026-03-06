package org.apache.dubbo.common.provider;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.common.conf.ClientType;
import org.apache.dubbo.common.conf.MessageType;
import org.apache.dubbo.common.cpu.SystemMonitorUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.dubbo.common.entry.CallResultManager;
import org.apache.dubbo.common.entry.Message;
import org.apache.dubbo.common.entry.PResult;
import org.apache.dubbo.common.entry.ProduceTestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
public class ProviderHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger log = LoggerFactory.getLogger(ProviderHandler.class);
    private final CallResultManager callResultManager = CallResultManager.getInstance();
    private String providerId;
    ProviderHandler(String providerId) {
        this.providerId = providerId;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        MessageType type = message.getType();
        if (type == MessageType.CONTROL) {
            PResult pResult = new PResult();
            SystemMonitorUtil.stop();
            Map<Integer, Integer> memoryUsage = SystemMonitorUtil.MEMORY_USAGE;
            Map<Integer, Integer> cpuUsage = SystemMonitorUtil.CPU_USAGE;
            Date startTime = SystemMonitorUtil.startTime;
            Date endTime = SystemMonitorUtil.endTime;
            Map<String, ProduceTestResult> allResults = callResultManager.getAllResults();
            pResult.setAllResults(allResults);
            pResult.setMemoryUsage(memoryUsage);
            pResult.setCpuUsage(cpuUsage);
            pResult.setCpuStartTime(startTime);
            pResult.setCpuEndTime(endTime);
            Message msg = new Message();
            msg.setType(MessageType.RESULT);
            msg.setClientType(ClientType.PROVIDER);
            msg.setClientId(providerId);
            msg.setData(JSON.toJSONString(pResult));
            channelHandlerContext.writeAndFlush(msg);
        }
    }
}
