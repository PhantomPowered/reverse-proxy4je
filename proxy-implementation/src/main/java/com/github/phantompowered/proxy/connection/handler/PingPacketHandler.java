package com.github.phantompowered.proxy.connection.handler;

import com.github.phantompowered.proxy.ImplementationUtil;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.ping.ServerPing;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.status.client.PacketStatusOutResponse;

public class PingPacketHandler {

    @PacketHandler(packetIds = ProtocolIds.ToClient.Status.SERVER_INFO, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.STATUS)
    public void handleStatusResponse(ServerPinger pinger, PacketStatusOutResponse response) {
        ServerPing serverPing = ImplementationUtil.GSON.fromJson(response.getResponse(), ServerPing.class);
        pinger.complete(serverPing);
    }

}
