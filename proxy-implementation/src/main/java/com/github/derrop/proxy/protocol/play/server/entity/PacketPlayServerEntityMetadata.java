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
package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;
import com.github.derrop.proxy.util.serialize.SerializableObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PacketPlayServerEntityMetadata implements Packet, EntityPacket {

    private int entityId;
    private Collection<SerializableObject> objects;

    public PacketPlayServerEntityMetadata(int entityId, Collection<SerializableObject> objects) {
        this.entityId = entityId;
        this.objects = objects;
    }

    public PacketPlayServerEntityMetadata() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_METADATA;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Collection<SerializableObject> getObjects() {
        return objects;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setObjects(Collection<SerializableObject> objects) {
        this.objects = objects;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.objects = MinecraftSerializableObjectList.readList(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        MinecraftSerializableObjectList.writeList(protoBuf, this.objects);
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityMetadata{" +
                "entityId=" + entityId +
                ", objects=" + objects +
                '}';
    }
}
