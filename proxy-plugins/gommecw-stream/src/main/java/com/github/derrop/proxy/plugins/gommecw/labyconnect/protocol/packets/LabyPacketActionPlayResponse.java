package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

public class LabyPacketActionPlayResponse extends LabyPacket {
    private short requestId;
    private boolean allowed;
    private String reason;

    public LabyPacketActionPlayResponse() {
    }

    public LabyPacketActionPlayResponse(boolean allowed) {
        this.allowed = allowed;
    }

    public short getRequestId() {
        return this.requestId;
    }

    public String getReason() {
        return this.reason;
    }

    public void read(ProtoBuf buf) {
        this.requestId = buf.readShort();
        this.allowed = buf.readBoolean();

        if (!this.allowed) {
            this.reason = LabyBufferUtils.readString(buf);
        }
    }


    public void write(ProtoBuf buf) {
        buf.writeShort(this.requestId);
        buf.writeBoolean(this.allowed);

        if (!this.allowed) {
            LabyBufferUtils.writeString(buf, this.reason);
        }
    }


    public void handle(PacketHandler handler) {
        //LabyMod.getInstance().getUserManager().resolveAction(this.requestId, this);
    }

    public boolean isAllowed() {
        return this.allowed;
    }
}


