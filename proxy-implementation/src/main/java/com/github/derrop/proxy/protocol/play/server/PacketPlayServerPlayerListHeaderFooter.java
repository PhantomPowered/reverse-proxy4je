package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerPlayerListHeaderFooter implements Packet {

    private String header;
    private String footer;

    public PacketPlayServerPlayerListHeaderFooter(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    public PacketPlayServerPlayerListHeaderFooter() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.PLAYER_LIST_HEADER_FOOTER;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFooter() {
        return this.footer;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.header = protoBuf.readString();
        this.footer = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.header);
        protoBuf.writeString(this.footer);
    }

    public String toString() {
        return "PacketPlayServerPlayerListHeaderFooter(header=" + this.getHeader() + ", footer=" + this.getFooter() + ")";
    }
}
