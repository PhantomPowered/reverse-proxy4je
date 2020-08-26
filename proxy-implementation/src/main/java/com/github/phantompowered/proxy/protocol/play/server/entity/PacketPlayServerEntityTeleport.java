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
package com.github.phantompowered.proxy.protocol.play.server.entity;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.util.PlayerPositionPacketUtil;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityTeleport implements Packet, EntityPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    public PacketPlayServerEntityTeleport(int entityId, @NotNull Location location) {
        this.entityId = entityId;

        this.x = PlayerPositionPacketUtil.getClientLocation(location.getX());
        this.y = PlayerPositionPacketUtil.getClientLocation(location.getY());
        this.z = PlayerPositionPacketUtil.getClientLocation(location.getZ());
        this.yaw = PlayerPositionPacketUtil.getClientRotation(location.getYaw());
        this.pitch = PlayerPositionPacketUtil.getClientRotation(location.getPitch());

        this.onGround = location.isOnGround();
    }

    public PacketPlayServerEntityTeleport(int entityId, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PacketPlayServerEntityTeleport() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_TELEPORT;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Location getLocation() {
        return new Location(
                PlayerPositionPacketUtil.getServerLocation(this.x),
                PlayerPositionPacketUtil.getServerLocation(this.y),
                PlayerPositionPacketUtil.getServerLocation(this.z),
                PlayerPositionPacketUtil.getServerRotation(this.yaw),
                PlayerPositionPacketUtil.getServerRotation(this.pitch),
                this.onGround
        );
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.yaw = protoBuf.readByte();
        this.pitch = protoBuf.readByte();
        this.onGround = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeBoolean(this.onGround);
    }

    public String toString() {
        return "PacketPlayServerEntityTeleport(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ")";
    }
}
