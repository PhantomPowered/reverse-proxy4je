package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketKick
        extends LabyPacket {
    private String cause;

    public LabyPacketKick(String cause) {
        this.cause = cause;
    }


    public LabyPacketKick() {
    }

    public void read(ProtoBuf buf) {
        this.cause = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.cause);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getReason() {
        return this.cause;
    }
}


