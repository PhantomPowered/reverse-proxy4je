package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

public class LabyPacketDisconnect extends LabyPacket {
    private String reason;

    public LabyPacketDisconnect() {
    }

    public LabyPacketDisconnect(String reason) {
        this.reason = reason;
    }


    public void read(ProtoBuf buf) {
        this.reason = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        if (getReason() == null) {
            LabyBufferUtils.writeString(buf, "Client Error");
            return;
        }
        LabyBufferUtils.writeString(buf, getReason());
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getReason() {
        return this.reason;
    }
}


