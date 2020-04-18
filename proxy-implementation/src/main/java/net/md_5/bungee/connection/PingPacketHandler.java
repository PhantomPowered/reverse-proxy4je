package net.md_5.bungee.connection;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import net.md_5.bungee.Util;

public class PingPacketHandler {

    @PacketHandler(packetIds = ProtocolIds.ToClient.Status.SERVER_INFO, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.STATUS)
    public void handleStatusResponse(ServerPinger pinger, PacketStatusOutResponse response) {
        ServerPing serverPing = Util.GSON.fromJson(response.getResponse(), ServerPing.class);
        pinger.complete(serverPing);
    }

}
