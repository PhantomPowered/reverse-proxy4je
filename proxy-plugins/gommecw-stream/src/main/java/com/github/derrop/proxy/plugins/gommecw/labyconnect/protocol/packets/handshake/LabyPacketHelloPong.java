package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketHelloPong
        extends LabyPacket {
    private long a;

    public LabyPacketHelloPong() {
    }

    public LabyPacketHelloPong(long a) {
        this.a = a;
    }


    public void read(ProtoBuf buf) {
        this.a = buf.readLong();
    }


    public void write(ProtoBuf buf) {
        buf.writeLong(this.a);
    }


    public int getId() {
        return 1;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


