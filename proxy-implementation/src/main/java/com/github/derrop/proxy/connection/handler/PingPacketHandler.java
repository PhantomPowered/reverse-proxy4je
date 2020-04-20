package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.derrop.proxy.util.Utils;

public class PingPacketHandler {

    @PacketHandler(packetIds = ProtocolIds.ToClient.Status.SERVER_INFO, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.STATUS)
    public void handleStatusResponse(ServerPinger pinger, PacketStatusOutResponse response) {
        ServerPing serverPing = Utils.GSON.fromJson(response.getResponse(), ServerPing.class);
        pinger.complete(serverPing);
    }

}
