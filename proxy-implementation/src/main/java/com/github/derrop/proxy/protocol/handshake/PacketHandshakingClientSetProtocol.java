package com.github.derrop.proxy.protocol.handshake;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketHandshakingClientSetProtocol implements Packet {

    private int protocolVersion;
    private String host;
    private int port;
    private int requestedProtocol;

    public PacketHandshakingClientSetProtocol(int protocolVersion, String host, int port, int requestedProtocol) {
        this.protocolVersion = protocolVersion;
        this.host = host;
        this.port = port;
        this.requestedProtocol = requestedProtocol;
    }

    public PacketHandshakingClientSetProtocol() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Handshaking.SET_PROTOCOL;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.protocolVersion = protoBuf.readVarInt();
        this.host = protoBuf.readString();
        this.port = protoBuf.readUnsignedShort();
        this.requestedProtocol = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.protocolVersion);
        protoBuf.writeString(this.host);
        protoBuf.writeShort(this.port);
        protoBuf.writeVarInt(this.requestedProtocol);
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public int getRequestedProtocol() {
        return this.requestedProtocol;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String toString() {
        return "PacketHandshakingInSetProtocol(protocolVersion=" + this.getProtocolVersion() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", requestedProtocol=" + this.getRequestedProtocol() + ")";
    }
}
