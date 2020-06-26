package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketBanned
        extends LabyPacket {
    private String reason;
    private long until;

    public LabyPacketBanned(String reason, long until) {
        this.reason = reason;
        this.until = until;
    }


    public LabyPacketBanned() {
    }


    public void read(ProtoBuf buf) {
        this.reason = LabyBufferUtils.readString(buf);
        this.until = buf.readLong();
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.reason);
        buf.writeLong(this.until);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getReason() {
        return this.reason;
    }


    public long getUntil() {
        return this.until;
    }
}


