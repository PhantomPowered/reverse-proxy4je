package com.github.derrop.proxy.protocol.play.server.entity.position;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityRelMove extends PacketPlayServerEntity {
    public PacketPlayServerEntityRelMove(int entityId, byte x, byte y, byte z, boolean onGround) {
        super(entityId);
        super.posX = x;
        super.posY = y;
        super.posZ = z;
        super.onGround = onGround;
    }

    public PacketPlayServerEntityRelMove() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        super.read(protoBuf, direction, protocolVersion);
        super.posX = protoBuf.readByte();
        super.posY = protoBuf.readByte();
        super.posZ = protoBuf.readByte();
        super.onGround = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        super.write(protoBuf, direction, protocolVersion);
        protoBuf.writeByte(super.posX);
        protoBuf.writeByte(super.posY);
        protoBuf.writeByte(super.posZ);
        protoBuf.writeBoolean(super.onGround);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_REL_MOVE;
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityRelMove{"
                + "entityId=" + entityId
                + ", posX=" + posX
                + ", posY=" + posY
                + ", posZ=" + posZ
                + ", onGround=" + onGround
                + ", hasRotation=false"
                + "} ";
    }
}
