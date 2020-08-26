package com.github.phantompowered.proxy.protocol.rewrite;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityDestroy;
import com.github.phantompowered.proxy.protocol.play.server.player.PacketPlayPlayerCombatEvent;

public class EntityRewrite18 extends EntityRewrite {

    @Override
    public void updatePacketToServer(Packet packet, int oldEntityId, int newEntityId) {
        if (packet instanceof EntityPacket) {
            this.rewriteEntityPacket((EntityPacket) packet, oldEntityId, newEntityId);
        }
    }

    @Override
    public void updatePacketToClient(Packet packet, int oldEntityId, int newEntityId) {
        if (packet instanceof EntityPacket) {
            this.rewriteEntityPacket((EntityPacket) packet, oldEntityId, newEntityId);
        } else if (packet instanceof PacketPlayServerEntityDestroy) {
            int[] entityIds = ((PacketPlayServerEntityDestroy) packet).getEntityIds();
            for (int i = 0; i < entityIds.length; i++) {
                entityIds[i] = this.getModifiedEntityId(entityIds[i], oldEntityId, newEntityId);
            }
        } else if (packet instanceof PacketPlayPlayerCombatEvent) {
            PacketPlayPlayerCombatEvent event = (PacketPlayPlayerCombatEvent) packet;
            event.setPlayerId(this.getModifiedEntityId(event.getPlayerId(), oldEntityId, newEntityId));
            event.setEntityId(this.getModifiedEntityId(event.getEntityId(), oldEntityId, newEntityId));
        }
    }

}
