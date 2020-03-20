package de.derrop.minecraft.proxy.connection.cache;

import io.netty.buffer.ByteBuf;

public class SpawnedEntity {

    private int targetPacketId;
    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private ByteBuf extraData;

    public SpawnedEntity(int targetPacketId, int entityId, int x, int y, int z, byte yaw, byte pitch, ByteBuf extraData) {
        this.targetPacketId = targetPacketId;
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.extraData = extraData;
    }

    public int getTargetPacketId() {
        return targetPacketId;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public byte getYaw() {
        return yaw;
    }

    public byte getPitch() {
        return pitch;
    }

    public ByteBuf getExtraData() {
        return extraData;
    }

    public void setTargetPacketId(int targetPacketId) {
        this.targetPacketId = targetPacketId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setExtraData(ByteBuf extraData) {
        this.extraData = extraData;
    }
}
