package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
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
