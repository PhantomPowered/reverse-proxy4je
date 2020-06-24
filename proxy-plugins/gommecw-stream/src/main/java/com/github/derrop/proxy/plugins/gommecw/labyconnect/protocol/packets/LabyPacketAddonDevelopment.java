package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.util.UUID;

public class LabyPacketAddonDevelopment extends LabyPacket {
    private UUID sender;
    private UUID[] receivers;
    private String key;
    private byte[] data; // compressed

    public LabyPacketAddonDevelopment() {
    }

    public LabyPacketAddonDevelopment(UUID sender, UUID[] receivers, String key, byte[] data) {
        this.sender = sender;
        this.receivers = receivers;
        this.key = key;
        this.data = data;
    }

    public UUID getSender() {
        return this.sender;
    }

    public UUID[] getReceivers() {
        return this.receivers;
    }

    public String getKey() {
        return this.key;
    }

    public void read(ProtoBuf buf) {
        this.sender = new UUID(buf.readLong(), buf.readLong());
        int receiverCnt = buf.readShort();
        this.receivers = new UUID[receiverCnt];
        for (int i = 0; i < this.receivers.length; i++) {
            this.receivers[i] = new UUID(buf.readLong(), buf.readLong());
        }
        this.key = LabyBufferUtils.readString(buf);
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        this.data = data;
    }


    public void write(ProtoBuf buf) {
        buf.writeLong(this.sender.getMostSignificantBits());
        buf.writeLong(this.sender.getLeastSignificantBits());
        buf.writeShort(this.receivers.length);
        for (UUID receiver : this.receivers) {
            buf.writeLong(receiver.getMostSignificantBits());
            buf.writeLong(receiver.getLeastSignificantBits());
        }

        LabyBufferUtils.writeString(buf, this.key);
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
    }


    public void handle(PacketHandler handler) {
    }

}


