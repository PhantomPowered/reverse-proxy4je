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
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.EntityPacket;
import com.github.derrop.proxy.protocol.play.server.entity.util.PlayerPositionPacketUtil;
import com.github.derrop.proxy.data.DataWatcher;
import com.github.derrop.proxy.data.DataWatcherEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public class PacketPlayServerNamedEntitySpawn implements PositionedPacket, EntityPacket {

    private int entityId;
    private UUID playerId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private short currentItem;
    private Collection<DataWatcherEntry> objects;

    public PacketPlayServerNamedEntitySpawn(int entityId, UUID playerId, int x, int y, int z, byte yaw, byte pitch, short currentItem, Collection<DataWatcherEntry> objects) {
        this.entityId = entityId;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.currentItem = currentItem;
        this.objects = objects;
    }

    public PacketPlayServerNamedEntitySpawn(int entityId, UUID playerId, Location location, short currentItem, Collection<DataWatcherEntry> objects) {
        this(entityId, playerId,
                PlayerPositionPacketUtil.getClientLocation(location.getX()), PlayerPositionPacketUtil.getClientLocation(location.getY()), PlayerPositionPacketUtil.getClientLocation(location.getZ()),
                PlayerPositionPacketUtil.getClientRotation(location.getYaw()), PlayerPositionPacketUtil.getClientRotation(location.getPitch()),
                currentItem, objects
        );
    }

    public PacketPlayServerNamedEntitySpawn() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.NAMED_ENTITY_SPAWN;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    public UUID getPlayerId() {
        return this.playerId;
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

    @Override
    public byte getYaw() {
        return this.yaw;
    }

    @Override
    public byte getPitch() {
        return this.pitch;
    }

    public short getCurrentItem() {
        return this.currentItem;
    }

    public Collection<DataWatcherEntry> getObjects() {
        return this.objects;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
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

    @Override
    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    @Override
    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setCurrentItem(short currentItem) {
        this.currentItem = currentItem;
    }

    public void setObjects(Collection<DataWatcherEntry> objects) {
        this.objects = objects;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.playerId = protoBuf.readUniqueId();

        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.yaw = protoBuf.readByte();
        this.pitch = protoBuf.readByte();
        this.currentItem = protoBuf.readShort();

        this.objects = DataWatcher.readList(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeUniqueId(this.playerId);

        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeShort(this.currentItem);

        DataWatcher.writeList(protoBuf, this.objects);
    }

    @Override
    public String toString() {
        return "PacketPlayServerNamedEntitySpawn{" +
                "entityId=" + entityId +
                ", playerId=" + playerId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", currentItem=" + currentItem +
                ", objects=" + objects +
                '}';
    }
}
