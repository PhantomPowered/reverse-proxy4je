package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardDisplay implements Packet {

    private byte position;
    private String name;

    public PacketPlayServerScoreboardDisplay(byte position, String name) {
        this.position = position;
        this.name = name;
    }

    public PacketPlayServerScoreboardDisplay() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_DISPLAY_OBJECTIVE;
    }

    public byte getPosition() {
        return this.position;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.position = protoBuf.readByte();
        this.name = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.position);
        protoBuf.writeString(this.name);
    }

    public String toString() {
        return "PacketPlayServerScoreboardDisplay(position=" + this.getPosition() + ", name=" + this.getName() + ")";
    }
}
