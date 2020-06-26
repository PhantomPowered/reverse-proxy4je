package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

public class LabyPacketActionPlay extends LabyPacket {
    private short requestId;
    private short actionType;
    private byte[] data;

    public LabyPacketActionPlay(short requestId, short actionType, byte[] data) {
        this.requestId = requestId;
        this.actionType = actionType;
        this.data = data;
    }

    public LabyPacketActionPlay() {
    }

    public short getRequestId() {
        return this.requestId;
    }


    public short getActionType() {
        return this.actionType;
    }

    public byte[] getData() {
        return this.data;
    }

    public void read(ProtoBuf buf) {
        this.requestId = buf.readShort();
        this.actionType = buf.readShort();

        int length = buf.readVarInt();

        if (length > 1024) {
            throw new RuntimeException("data array too big");
        }
        this.data = new byte[length];
        buf.readBytes(this.data);
    }


    public void write(ProtoBuf buf) {
        buf.writeShort(this.requestId);
        buf.writeShort(this.actionType);

        if (this.data == null) {
            buf.writeVarInt(0);
        } else {
            buf.writeVarInt(this.data.length);
            buf.writeBytes(this.data);
        }
    }

    public void handle(PacketHandler handler) {
    }
}


