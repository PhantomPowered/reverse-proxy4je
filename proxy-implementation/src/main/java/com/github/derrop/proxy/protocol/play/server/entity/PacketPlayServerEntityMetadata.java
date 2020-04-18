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
import com.github.derrop.proxy.util.DataWatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PacketPlayServerEntityMetadata implements Packet {

    private int entityId;
    private List<DataWatcher.WatchableObject> watchableObjects;

    public PacketPlayServerEntityMetadata(int entityId, List<DataWatcher.WatchableObject> watchableObjects) {
        this.entityId = entityId;
        this.watchableObjects = watchableObjects;
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

    public List<DataWatcher.WatchableObject> getWatchableObjects() {
        return this.watchableObjects;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setWatchableObjects(List<DataWatcher.WatchableObject> watchableObjects) {
        this.watchableObjects = watchableObjects;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();

        try {
            this.watchableObjects = DataWatcher.readWatchedListFromByteBuf(protoBuf);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);

        try {
            DataWatcher.writeWatchedListToByteBuf(this.watchableObjects, protoBuf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String toString() {
        return "PacketPlayServerEntityMetadata(entityId=" + this.getEntityId() + ", watchableObjects=" + this.getWatchableObjects() + ")";
    }
}
