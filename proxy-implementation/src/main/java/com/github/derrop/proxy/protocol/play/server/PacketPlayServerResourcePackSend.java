package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerResourcePackSend implements Packet {

    private String url;
    private String hash;

    public PacketPlayServerResourcePackSend(String url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    public PacketPlayServerResourcePackSend() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.RESOURCE_PACK_SEND;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.url = protoBuf.readString();
        this.hash = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.url);
        protoBuf.writeString(this.hash);
    }

    public String toString() {
        return "PacketPlayServerResourcePackSend(url=" + this.getUrl() + ", hash=" + this.getHash() + ")";
    }
}
