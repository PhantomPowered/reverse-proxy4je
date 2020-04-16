package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

public class PacketPlayServerPluginMessage implements Packet {

    private String tag;
    private byte[] data;

    public PacketPlayServerPluginMessage(String tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }

    public PacketPlayServerPluginMessage() {
    }

    public DataInput getStream() {
        return new DataInputStream(new ByteArrayInputStream(data));
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.tag = protoBuf.readString();

        int maxLength = this.getId() == ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD ? 1048576 : Short.MAX_VALUE;
        if (protoBuf.readableBytes() > maxLength) {
            this.data = new byte[0];
            return;
        }

        this.data = new byte[protoBuf.readableBytes()];
        protoBuf.readBytes(this.data);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.tag);
        protoBuf.writeBytes(this.data);
    }

    public String getTag() {
        return this.tag;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String toString() {
        return "PacketPlayServerPluginMessage(tag=" + this.getTag() + ", data=" + java.util.Arrays.toString(this.getData()) + ")";
    }
}
