package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

public class LabyPacketUpdateCosmetics extends LabyPacket {
    private String json = null;

    public LabyPacketUpdateCosmetics(String json) {
        this.json = json;
    }

    public LabyPacketUpdateCosmetics() {
    }

    public String getJson() {
        return this.json;
    }

    public void read(ProtoBuf buf) {
        boolean hasJsonString = buf.readBoolean();
        if (hasJsonString) {
            this.json = LabyBufferUtils.readString(buf);
        }
    }

    public void write(ProtoBuf buf) {
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


