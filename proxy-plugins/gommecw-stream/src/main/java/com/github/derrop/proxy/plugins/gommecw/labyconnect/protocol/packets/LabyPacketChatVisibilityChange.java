package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketChatVisibilityChange
        extends LabyPacket {
    private boolean visible;

    public void read(ProtoBuf buf) {
        this.visible = buf.readBoolean();
    }


    public void write(ProtoBuf buf) {
        buf.writeBoolean(this.visible);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public boolean isVisible() {
        return this.visible;
    }
}


