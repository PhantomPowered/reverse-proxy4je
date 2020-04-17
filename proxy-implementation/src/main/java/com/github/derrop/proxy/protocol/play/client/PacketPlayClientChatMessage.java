package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientChatMessage implements Packet {

    private String message;

    public PacketPlayClientChatMessage(String message) {
        this.message = message;
    }

    public PacketPlayClientChatMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.CHAT;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.message = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        if (this.message.length() > 100) {
            this.message = this.message.substring(0, 100);
        }

        protoBuf.writeString(this.message);
    }

    public String toString() {
        return "PacketPlayClientChatMessage()";
    }
}
