package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerCamera implements Packet {

    private int entityId;

    public PacketPlayServerCamera(int entityId) {
        this.entityId = entityId;
    }

    public PacketPlayServerCamera() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CAMERA;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
    }

    public String toString() {
        return "PacketPlayServerCamera(entityId=" + this.getEntityId() + ")";
    }
}
