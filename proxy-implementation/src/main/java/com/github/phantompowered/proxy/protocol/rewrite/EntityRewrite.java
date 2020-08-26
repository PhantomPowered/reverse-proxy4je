package com.github.phantompowered.proxy.protocol.rewrite;

import com.github.phantompowered.proxy.api.network.Packet;

// this is necessary for switching the clients without reconnecting
public abstract class EntityRewrite {

    public abstract void updatePacketToServer(Packet packet, int oldEntityId, int newEntityId);

    public abstract void updatePacketToClient(Packet packet, int oldEntityId, int newEntityId);

}
