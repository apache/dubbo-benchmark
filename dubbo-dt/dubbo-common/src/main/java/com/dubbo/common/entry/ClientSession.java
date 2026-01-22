package com.dubbo.common.entry;

import com.dubbo.common.conf.ClientType;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;



@Data
public class ClientSession {
        private final String clientId;
        private final Channel channel;
        private final ClientType type;
        private final long connectTime;
        private final AtomicLong lastHeartbeatTime = new AtomicLong(System.currentTimeMillis());
        private static final long HEARTBEAT_TIMEOUT = 60 * 1000;
        
        public ClientSession(String clientId, Channel channel, ClientType type) {
            this.clientId = clientId;
            this.channel = channel;
            this.type = type;
            this.connectTime = System.currentTimeMillis();
        }
        public void updateHeartbeat() {
            lastHeartbeatTime.set(System.currentTimeMillis());
        }
        public boolean isHeartbeatTimeout() {
            return System.currentTimeMillis() - lastHeartbeatTime.get() > HEARTBEAT_TIMEOUT;
        }

        public boolean isOnline() {
            return channel.isActive() && !isHeartbeatTimeout();
        }
    }