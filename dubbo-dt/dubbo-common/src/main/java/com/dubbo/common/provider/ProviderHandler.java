package com.dubbo.common.provider;

import com.alibaba.fastjson2.JSON;
import com.dubbo.common.conf.ClientType;
import com.dubbo.common.conf.MessageType;
import com.dubbo.common.cpu.SystemMonitorUtil;
import com.dubbo.common.entry.CallResultManager;
import com.dubbo.common.entry.Message;
import com.dubbo.common.entry.PResult;
import com.dubbo.common.entry.ProduceTestResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
        if (message.getType() == MessageType.CONTROL) {
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
