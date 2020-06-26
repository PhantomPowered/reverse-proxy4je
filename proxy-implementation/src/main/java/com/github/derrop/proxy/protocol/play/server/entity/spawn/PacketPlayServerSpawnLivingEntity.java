/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.EntityPacket;
import com.github.derrop.proxy.data.DataWatcher;
import com.github.derrop.proxy.data.DataWatcherEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PacketPlayServerSpawnLivingEntity implements PositionedPacket, EntityPacket {

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
    private Collection<DataWatcherEntry> objects;

    public PacketPlayServerSpawnLivingEntity(int entityId, int x, int y, int z, int type, int velocityX, int velocityY, int velocityZ, byte yaw, byte pitch, byte headPitch, Collection<DataWatcherEntry> objects) {
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
        this.objects = objects;
    }

    public PacketPlayServerSpawnLivingEntity() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_LIVING;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
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

    @Override
    public byte getYaw() {
        return this.yaw;
    }

    @Override
    public byte getPitch() {
        return this.pitch;
    }

    public byte getHeadPitch() {
        return this.headPitch;
    }

    public Collection<DataWatcherEntry> getObjects() {
        return this.objects;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
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

    @Override
    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    @Override
    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setHeadPitch(byte headPitch) {
        this.headPitch = headPitch;
    }

    public void setObjects(Collection<DataWatcherEntry> objects) {
        this.objects = objects;
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

        this.objects = DataWatcher.readList(protoBuf);
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

        DataWatcher.writeList(protoBuf, this.objects);
    }

    @Override
    public String toString() {
        return "PacketPlayServerSpawnLivingEntity{" +
                "entityId=" + entityId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", type=" + type +
                ", velocityX=" + velocityX +
                ", velocityY=" + velocityY +
                ", velocityZ=" + velocityZ +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", headPitch=" + headPitch +
                ", objects=" + objects +
                '}';
    }
}
