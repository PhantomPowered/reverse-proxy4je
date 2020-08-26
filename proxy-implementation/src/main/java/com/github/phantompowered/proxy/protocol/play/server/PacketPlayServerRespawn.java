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
package com.github.phantompowered.proxy.protocol.play.server;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerRespawn implements Packet {

    private int dimension;
    private short difficulty;
    private short gameMode;
    private String levelType;

    public PacketPlayServerRespawn(int dimension, short difficulty, short gameMode, String levelType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.levelType = levelType;
    }

    public PacketPlayServerRespawn() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.RESPAWN;
    }

    public int getDimension() {
        return this.dimension;
    }

    public short getDifficulty() {
        return this.difficulty;
    }

    public short getGameMode() {
        return this.gameMode;
    }

    public String getLevelType() {
        return this.levelType;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.dimension = protoBuf.readInt();
        this.difficulty = protoBuf.readUnsignedByte();
        this.gameMode = protoBuf.readUnsignedByte();
        this.levelType = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.dimension);
        protoBuf.writeByte(this.difficulty);
        protoBuf.writeByte(this.gameMode);
        protoBuf.writeString(this.levelType);
    }

    @Override
    public String toString() {
        return "PacketPlayServerRespawn{" + "dimension=" + dimension + ", difficulty=" + difficulty + ", gameMode=" + gameMode + ", levelType='" + levelType + '\'' + '}';
    }
}
