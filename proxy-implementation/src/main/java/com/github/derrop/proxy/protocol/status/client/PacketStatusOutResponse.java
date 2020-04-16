package com.github.derrop.proxy.protocol.status.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketStatusOutResponse implements Packet {

    private String response;

    public PacketStatusOutResponse(String response) {
        this.response = response;
    }

    public PacketStatusOutResponse() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Status.SERVER_INFO;
    }

    public String getResponse() {
        return this.response;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.response = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.response);
    }

    public String toString() {
        return "PacketStatusOutResponse(response=" + this.getResponse() + ")";
    }
}
