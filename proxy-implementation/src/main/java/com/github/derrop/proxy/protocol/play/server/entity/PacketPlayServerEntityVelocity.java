package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityVelocity implements Packet {

    private int entityId;
    private int motionX;
    private int motionY;
    private int motionZ;

    public PacketPlayServerEntityVelocity(int entityId, double motionX, double motionY, double motionZ) {
        this.entityId = entityId;

        double limit = 3.9;

        if (motionX < -limit) {
            motionX = -limit;
        }

        if (motionY < -limit) {
            motionY = -limit;
        }

        if (motionZ < -limit) {
            motionZ = -limit;
        }

        if (motionX > limit) {
            motionX = limit;
        }

        if (motionY > limit) {
            motionY = limit;
        }

        if (motionZ > limit) {
            motionZ = limit;
        }

        this.motionX = (int) (motionX * 8000.0D);
        this.motionY = (int) (motionY * 8000.0D);
        this.motionZ = (int) (motionZ * 8000.0D);
    }

    public PacketPlayServerEntityVelocity() {
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getMotionX() {
        return motionX;
    }

    public void setMotionX(int motionX) {
        this.motionX = motionX;
    }

    public int getMotionY() {
        return motionY;
    }

    public void setMotionY(int motionY) {
        this.motionY = motionY;
    }

    public int getMotionZ() {
        return motionZ;
    }

    public void setMotionZ(int motionZ) {
        this.motionZ = motionZ;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.motionX = protoBuf.readShort();
        this.motionY = protoBuf.readShort();
        this.motionZ = protoBuf.readShort();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeShort(this.motionX);
        protoBuf.writeShort(this.motionY);
        protoBuf.writeShort(this.motionZ);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_VELOCITY;
    }
}
