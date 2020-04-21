package com.github.derrop.proxy.protocol.rewrite;

import com.github.derrop.proxy.api.network.Packet;

// this is necessary for switching the clients without reconnecting
public abstract class EntityRewrite {

    public abstract void updatePacketToServer(Packet packet, int oldEntityId, int newEntityId);

    public abstract void updatePacketToClient(Packet packet, int oldEntityId, int newEntityId);

}
