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
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.data.DataWatcher;
import com.github.phantompowered.proxy.data.DataWatcherEntry;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PacketPlayServerEntityMetadata implements Packet, EntityPacket {

    private int entityId;
    private Collection<DataWatcherEntry> objects;

    public PacketPlayServerEntityMetadata(int entityId, Collection<DataWatcherEntry> objects) {
        this.entityId = entityId;
        this.objects = objects;
    }

    public PacketPlayServerEntityMetadata() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY_METADATA;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Collection<DataWatcherEntry> getObjects() {
        return objects;
    }

    public void setObjects(Collection<DataWatcherEntry> objects) {
        this.objects = objects;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.objects = DataWatcher.readList(protoBuf);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        DataWatcher.writeList(protoBuf, this.objects);
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntityMetadata{"
                + "entityId=" + entityId
                + ", objects=" + objects
                + '}';
    }
}
