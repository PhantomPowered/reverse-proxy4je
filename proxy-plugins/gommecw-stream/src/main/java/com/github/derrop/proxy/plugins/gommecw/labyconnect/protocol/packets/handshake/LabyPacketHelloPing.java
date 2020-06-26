package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketHelloPing
        extends LabyPacket {
    private long a;

    public LabyPacketHelloPing() {
    }

    public LabyPacketHelloPing(long a) {
    }

    public void read(ProtoBuf buf) {
        this.a = buf.readLong();
        buf.readInt();
    }


    public void write(ProtoBuf buf) {
        buf.writeLong(this.a);
        buf.writeInt(23);
    }


    public int getId() {
        return 0;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


