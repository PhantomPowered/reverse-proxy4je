package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketPlayRequestRemove
        extends LabyPacket {
    private String playerName;

    public LabyPacketPlayRequestRemove(String playerName) {
        this.playerName = playerName;
    }


    public LabyPacketPlayRequestRemove() {
    }


    public void read(ProtoBuf buf) {
        this.playerName = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.playerName);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getPlayerName() {
        return this.playerName;
    }
}


