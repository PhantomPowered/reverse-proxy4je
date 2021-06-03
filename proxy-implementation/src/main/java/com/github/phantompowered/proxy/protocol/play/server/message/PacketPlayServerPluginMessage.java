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
package com.github.phantompowered.proxy.protocol.play.server.message;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

public class PacketPlayServerPluginMessage implements Packet {

    private String tag;
    private byte[] data;

    public PacketPlayServerPluginMessage(String tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }

    public PacketPlayServerPluginMessage() {
    }

    public DataInput getStream() {
        return new DataInputStream(new ByteArrayInputStream(data));
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.tag = protoBuf.readString();

        int maxLength = this.getId() == ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD ? 1048576 : Short.MAX_VALUE;
        if (protoBuf.readableBytes() > maxLength) {
            this.data = new byte[0];
            return;
        }

        this.data = new byte[protoBuf.readableBytes()];
        protoBuf.readBytes(this.data);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.tag);
        protoBuf.writeBytes(this.data);
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String toString() {
        return "PacketPlayServerPluginMessage(tag=" + this.getTag() + ", data=" + new String(this.getData()) + ")";
    }
}
