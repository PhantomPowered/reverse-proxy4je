package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.reconnect.ReconnectProfile;
import com.github.derrop.proxy.protocol.play.shared.PacketPlayKeepAlive;

import java.util.concurrent.TimeUnit;

public final class EntityTickHandler {

    private EntityTickHandler() {
        throw new UnsupportedOperationException();
    }

    public static void startTick() {
        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            for (BasicServiceConnection onlineClient : MCProxy.getInstance().getOnlineClients()) {
                onlineClient.getClient().keepAliveTick();
                if (onlineClient.getPlayer() != null) {
                    onlineClient.getPlayer().sendPacket(new PacketPlayKeepAlive(System.nanoTime())); // TODO: wait for result (if no, disconnect)
                }
            }

            for (ReconnectProfile value : MCProxy.getInstance().getReconnectProfiles().values()) {
                if (System.currentTimeMillis() >= value.getTimeout()) {
                    MCProxy.getInstance().getReconnectProfiles().remove(value.getUniqueId());
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
}
