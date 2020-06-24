package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

public class LabyPacketMojangStatus
        extends LabyPacket {
    public void read(ProtoBuf buf) {
        buf.readInt();
        LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


