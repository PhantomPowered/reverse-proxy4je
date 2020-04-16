package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerChatMessage implements Packet {

    private String message;
    private byte position;

    public PacketPlayServerChatMessage(String message) {
        this(message, (byte) 0);
    }

    public PacketPlayServerChatMessage(String message, byte position) {
        this.message = message;
        this.position = position;
    }

    public PacketPlayServerChatMessage() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CHAT;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.message = protoBuf.readString();
        this.position = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.message);
        protoBuf.writeByte(this.position);
    }

    public String getMessage() {
        return this.message;
    }

    public byte getPosition() {
        return this.position;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "PacketPlayServerChatMessage(message=" + this.getMessage() + ", position=" + this.getPosition() + ")";
    }
}
