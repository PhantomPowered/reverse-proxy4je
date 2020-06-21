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
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntity implements PositionedPacket, EntityPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private byte pitch;
    private byte yaw;
    private int type;
    private int extraData;

    public PacketPlayServerSpawnEntity(int entityId, int x, int y, int z, int speedX, int speedY, int speedZ, byte pitch, byte yaw, int type, int extraData) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.type = type;
        this.extraData = extraData;
    }

    public PacketPlayServerSpawnEntity() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY;
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

    public int getSpeedX() {
        return this.speedX;
    }

    public int getSpeedY() {
        return this.speedY;
    }

    public int getSpeedZ() {
        return this.speedZ;
    }

    @Override
    public byte getPitch() {
        return this.pitch;
    }

    @Override
    public byte getYaw() {
        return this.yaw;
    }

    public int getType() {
        return this.type;
    }

    public int getExtraData() {
        return this.extraData;
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

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public void setSpeedZ(int speedZ) {
        this.speedZ = speedZ;
    }

    @Override
    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    @Override
    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setExtraData(int extraData) {
        this.extraData = extraData;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.type = protoBuf.readByte();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.pitch = protoBuf.readByte();
        this.yaw = protoBuf.readByte();
        this.extraData = protoBuf.readInt();

        if (this.extraData > 0) {
            this.speedX = protoBuf.readShort();
            this.speedY = protoBuf.readShort();
            this.speedZ = protoBuf.readShort();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.type);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeInt(this.extraData);

        if (this.extraData > 0) {
            protoBuf.writeShort(this.speedX);
            protoBuf.writeShort(this.speedY);
            protoBuf.writeShort(this.speedZ);
        }
    }

    public String toString() {
        return "PacketPlayServerSpawnEntity(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", speedX=" + this.getSpeedX() + ", speedY=" + this.getSpeedY() + ", speedZ=" + this.getSpeedZ() + ", pitch=" + this.getPitch() + ", yaw=" + this.getYaw() + ", type=" + this.getType() + ", trackedEntityId=" + this.getExtraData() + ")";
    }
}
