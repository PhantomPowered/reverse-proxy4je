package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketServerMessage
        extends LabyPacket {
    private String message;

    public LabyPacketServerMessage(String message) {
        this.message = message;
    }


    public LabyPacketServerMessage() {
    }


    public void read(ProtoBuf buf) {
        this.message = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.message);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getMessage() {
        return this.message;
    }
}


