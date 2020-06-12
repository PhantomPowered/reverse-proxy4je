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
package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerLogin implements Packet {

    private int entityId;
    private short gameMode;
    private int dimension;
    private short difficulty;
    private short maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    public PacketPlayServerLogin(int entityId, short gameMode, int dimension, short difficulty, short maxPlayers, String levelType, boolean reducedDebugInfo) {
        this.entityId = entityId;
        this.gameMode = gameMode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public PacketPlayServerLogin() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.LOGIN;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public short getGameMode() {
        return this.gameMode;
    }

    public int getDimension() {
        return this.dimension;
    }

    public short getDifficulty() {
        return this.difficulty;
    }

    public String getLevelType() {
        return this.levelType;
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setGameMode(short gameMode) {
        this.gameMode = gameMode;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setDifficulty(short difficulty) {
        this.difficulty = difficulty;
    }

    public short getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(short maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readInt();
        this.gameMode = protoBuf.readUnsignedByte();
        this.dimension = protoBuf.readByte();
        this.difficulty = protoBuf.readUnsignedByte();
        this.maxPlayers = protoBuf.readUnsignedByte();
        this.levelType = protoBuf.readString();
        this.reducedDebugInfo = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.entityId);
        protoBuf.writeByte(this.gameMode);
        protoBuf.writeByte(this.dimension);
        protoBuf.writeByte(this.difficulty);
        protoBuf.writeByte(this.maxPlayers);
        protoBuf.writeString(this.levelType);
        protoBuf.writeBoolean(this.reducedDebugInfo);
    }

    public String toString() {
        return "PacketPlayServerLogin{" + "entityId=" + entityId + ", gameMode=" + gameMode + ", dimension=" + dimension + ", difficulty=" + difficulty + ", maxPlayers=" + maxPlayers + ", levelType='" + levelType + '\'' + ", reducedDebugInfo=" + reducedDebugInfo + '}';
    }
}
