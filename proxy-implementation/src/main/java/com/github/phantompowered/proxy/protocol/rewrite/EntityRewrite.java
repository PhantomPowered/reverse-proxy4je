package com.github.phantompowered.proxy.protocol.rewrite;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;

// this is necessary for switching the clients without reconnecting
public abstract class EntityRewrite {

    public abstract void updatePacketToServer(Packet packet, int oldEntityId, int newEntityId);

    public abstract void updatePacketToClient(Packet packet, int oldEntityId, int newEntityId);

    protected void rewriteEntityPacket(EntityPacket entity, int oldEntityId, int newEntityId) {
        if (entity.getEntityId() == -1) {
            return;
        }

        entity.setEntityId(this.getModifiedEntityId(entity.getEntityId(), oldEntityId, newEntityId));
    }

    protected int getModifiedEntityId(int currentId, int oldId, int newId) {
        return currentId == oldId ? newId : currentId == newId ? oldId : currentId;
    }

}
