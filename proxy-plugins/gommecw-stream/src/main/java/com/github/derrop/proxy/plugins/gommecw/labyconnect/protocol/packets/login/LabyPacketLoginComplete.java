package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketLoginComplete
        extends LabyPacket {
    private String dashboardPin;

    public LabyPacketLoginComplete(String string) {
        this.dashboardPin = string;
    }


    public LabyPacketLoginComplete() {
    }


    public void read(ProtoBuf buf) {
        this.dashboardPin = LabyBufferUtils.readString(buf);
    }

    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.dashboardPin);
    }


    public int getId() {
        return 2;
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getDashboardPin() {
        return this.dashboardPin;
    }
}


