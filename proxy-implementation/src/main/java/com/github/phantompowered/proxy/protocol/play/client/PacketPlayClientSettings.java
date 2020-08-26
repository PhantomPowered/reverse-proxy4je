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
package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientSettings implements Packet {

    private String locale;
    private byte viewDistance;
    private int chatFlags;
    private boolean chatColours;
    private byte difficulty;
    private byte skinParts;

    public PacketPlayClientSettings(String locale, byte viewDistance, int chatFlags, boolean chatColours, byte difficulty, byte skinParts) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColours = chatColours;
        this.difficulty = difficulty;
        this.skinParts = skinParts;
    }

    public PacketPlayClientSettings() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.SETTINGS;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.locale = protoBuf.readString();
        this.viewDistance = protoBuf.readByte();
        this.chatFlags = protoBuf.readUnsignedByte();
        this.chatColours = protoBuf.readBoolean();
        this.skinParts = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.locale);
        protoBuf.writeByte(this.viewDistance);
        protoBuf.writeByte(this.chatFlags);
        protoBuf.writeBoolean(this.chatColours);
        protoBuf.writeByte(this.skinParts);
    }

    public String getLocale() {
        return this.locale;
    }

    public byte getViewDistance() {
        return this.viewDistance;
    }

    public int getChatFlags() {
        return this.chatFlags;
    }

    public boolean isChatColours() {
        return this.chatColours;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public byte getSkinParts() {
        return this.skinParts;
    }

    public String toString() {
        return "PacketPlayClientSettings(locale=" + this.getLocale() + ", viewDistance=" + this.getViewDistance() + ", chatFlags=" + this.getChatFlags() + ", chatColours=" + this.isChatColours() + ", difficulty=" + this.getDifficulty() + ", skinParts=" + this.getSkinParts() + ")";
    }
}
