package com.github.derrop.proxy.protocol.play.server.entity.position;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityLook extends PacketPlayServerEntity {

    public PacketPlayServerEntityLook(int entityId, byte yaw, byte pitch, boolean onGround) {
        super(entityId);
        super.yaw = yaw;
        super.pitch = pitch;
        super.onGround = onGround;
        super.hasRotation = true;
    }

    public PacketPlayServerEntityLook() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        super.read(protoBuf, direction, protocolVersion);
        super.yaw = protoBuf.readByte();
        super.pitch = protoBuf.readByte();
        super.onGround = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        super.write(protoBuf, direction, protocolVersion);
        protoBuf.writeByte(super.yaw);
        protoBuf.writeByte(super.pitch);
        protoBuf.writeBoolean(super.onGround);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_LOOK;
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityLook{" +
                "entityId=" + entityId +
                ", posX=" + posX +
                ", posY=" + posY +
                ", posZ=" + posZ +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                ", hasRotation=true" +
                "} ";
    }
}
