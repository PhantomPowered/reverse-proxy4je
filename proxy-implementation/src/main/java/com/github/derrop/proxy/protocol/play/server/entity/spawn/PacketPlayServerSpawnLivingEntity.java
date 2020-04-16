package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.DataWatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PacketPlayServerSpawnLivingEntity implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int type;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private List<DataWatcher.WatchableObject> watcher;

    public PacketPlayServerSpawnLivingEntity(int entityId, int x, int y, int z, int type, int velocityX, int velocityY, int velocityZ, byte yaw, byte pitch, byte headPitch, List<DataWatcher.WatchableObject> watcher) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.headPitch = headPitch;
        this.watcher = watcher;
    }

    public PacketPlayServerSpawnLivingEntity() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_LIVING;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getType() {
        return this.type;
    }

    public int getVelocityX() {
        return this.velocityX;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public int getVelocityZ() {
        return this.velocityZ;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getHeadPitch() {
        return this.headPitch;
    }

    public List<DataWatcher.WatchableObject> getWatcher() {
        return this.watcher;
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

    public void setType(int type) {
        this.type = type;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public void setVelocityZ(int velocityZ) {
        this.velocityZ = velocityZ;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setHeadPitch(byte headPitch) {
        this.headPitch = headPitch;
    }

    public void setWatcher(List<DataWatcher.WatchableObject> watcher) {
        this.watcher = watcher;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.type = protoBuf.readByte() & 255;
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.yaw = protoBuf.readByte();
        this.pitch = protoBuf.readByte();
        this.headPitch = protoBuf.readByte();
        this.velocityX = protoBuf.readShort();
        this.velocityY = protoBuf.readShort();
        this.velocityZ = protoBuf.readShort();

        try {
            this.watcher = DataWatcher.readWatchedListFromByteBuf(protoBuf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.type & 255);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeByte(this.headPitch);
        protoBuf.writeShort(this.velocityX);
        protoBuf.writeShort(this.velocityY);
        protoBuf.writeShort(this.velocityZ);

        try {
            DataWatcher.writeWatchedListToByteBuf(this.watcher, protoBuf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String toString() {
        return "PacketPlayServerSpawnLivingEntity(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", type=" + this.getType() + ", velocityX=" + this.getVelocityX() + ", velocityY=" + this.getVelocityY() + ", velocityZ=" + this.getVelocityZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", headPitch=" + this.getHeadPitch() + ", watcher=" + this.getWatcher() + ")";
    }
}
