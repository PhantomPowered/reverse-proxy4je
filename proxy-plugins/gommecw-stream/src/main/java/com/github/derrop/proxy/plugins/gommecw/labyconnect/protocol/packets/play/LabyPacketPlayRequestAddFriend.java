package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketPlayRequestAddFriend
        extends LabyPacket {
    private String name;

    public LabyPacketPlayRequestAddFriend(String name) {
        this.name = name;
    }


    public LabyPacketPlayRequestAddFriend() {
    }


    public void read(ProtoBuf buf) {
        byte[] a = new byte[buf.readInt()];
        for (int i = 0; i < a.length; i++) {
            a[i] = buf.readByte();
        }
        this.name = new String(a);
    }


    public void write(ProtoBuf buf) {
        buf.writeInt((this.name.getBytes()).length);
        buf.writeBytes(this.name.getBytes());
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getName() {
        return this.name;
    }
}


