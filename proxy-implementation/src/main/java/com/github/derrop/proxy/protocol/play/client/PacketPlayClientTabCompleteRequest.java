package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientTabCompleteRequest implements Packet {

    private String cursor;
    private boolean hasPosition;
    private long position;

    public PacketPlayClientTabCompleteRequest(String cursor) {
        this.cursor = cursor;
    }

    public PacketPlayClientTabCompleteRequest(String cursor, boolean hasPosition, long position) {
        this.cursor = cursor;
        this.hasPosition = hasPosition;
        this.position = position;
    }

    public PacketPlayClientTabCompleteRequest() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.TAB_COMPLETE;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.cursor = protoBuf.readString();
        this.hasPosition = protoBuf.readBoolean();

        if (this.hasPosition) {
            this.position = protoBuf.readLong();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.cursor);
        protoBuf.writeBoolean(this.hasPosition);

        if (this.hasPosition) {
            protoBuf.writeLong(this.position);
        }
    }

    public String getCursor() {
        return this.cursor;
    }

    public boolean isHasPosition() {
        return this.hasPosition;
    }

    public long getPosition() {
        return this.position;
    }

    public String toString() {
        return "PacketPlayClientTabCompleteRequest(cursor=" + this.getCursor() + ", hasPosition=" + this.isHasPosition() + ", position=" + this.getPosition() + ")";
    }
}
