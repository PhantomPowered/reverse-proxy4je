package com.github.derrop.proxy.protocol.play.server.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSetDifficulty implements Packet {

    private int difficulty;

    public PacketPlayServerSetDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public PacketPlayServerSetDifficulty() {
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.difficulty = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.difficulty);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SERVER_DIFFICULTY;
    }

    @Override
    public String toString() {
        return "PacketPlayServerSetDifficulty{" +
                "difficulty=" + difficulty +
                '}';
    }
}
