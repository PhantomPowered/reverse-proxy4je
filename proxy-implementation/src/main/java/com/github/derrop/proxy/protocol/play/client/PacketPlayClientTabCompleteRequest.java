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
package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientTabCompleteRequest implements Packet {

    private String cursor;
    private boolean hasPosition;
    private long position;

    public PacketPlayClientTabCompleteRequest(String cursor) {
        this.cursor = cursor;
    }

    public PacketPlayClientTabCompleteRequest(String cursor, boolean hasPosition, long position) {
        this.cursor = cursor;
        this.hasPosition = hasPosition;
        this.position = position;
    }

    public PacketPlayClientTabCompleteRequest() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.TAB_COMPLETE;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.cursor = protoBuf.readString();
        this.hasPosition = protoBuf.readBoolean();

        if (this.hasPosition) {
            this.position = protoBuf.readLong();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.cursor);
        protoBuf.writeBoolean(this.hasPosition);

        if (this.hasPosition) {
            protoBuf.writeLong(this.position);
        }
    }

    public String getCursor() {
        return this.cursor;
    }

    public boolean isHasPosition() {
        return this.hasPosition;
    }

    public long getPosition() {
        return this.position;
    }

    public String toString() {
        return "PacketPlayClientTabCompleteRequest(cursor=" + this.getCursor() + ", hasPosition=" + this.isHasPosition() + ", position=" + this.getPosition() + ")";
    }
}
