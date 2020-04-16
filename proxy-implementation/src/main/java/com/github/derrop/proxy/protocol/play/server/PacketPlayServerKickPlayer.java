package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerKickPlayer implements Packet {

    private String message;

    public PacketPlayServerKickPlayer(String message) {
        this.message = message;
    }

    public PacketPlayServerKickPlayer() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.KICK_DISCONNECT;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.message = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.message);
    }

    public String toString() {
        return "PacketPlayServerKickPlayer(message=" + this.getMessage() + ")";
    }
}
