package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketPlayRequestAddFriendResponse
        extends LabyPacket {
    private String searched;
    private boolean requestSent;
    private String reason;

    public LabyPacketPlayRequestAddFriendResponse(String searched, boolean sent) {
        this.searched = searched;
        this.requestSent = sent;
    }


    public LabyPacketPlayRequestAddFriendResponse(String searched, boolean sent, String reason) {
        this.searched = searched;
        this.requestSent = sent;
        this.reason = reason;
    }


    public LabyPacketPlayRequestAddFriendResponse() {
    }


    public void read(ProtoBuf buf) {
        this.searched = LabyBufferUtils.readString(buf);
        this.requestSent = buf.readBoolean();
        if (!this.requestSent) {
            this.reason = LabyBufferUtils.readString(buf);
        }
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.searched);
        buf.writeBoolean(this.requestSent);
        if (!isRequestSent()) {
            LabyBufferUtils.writeString(buf, this.reason);
        }
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public boolean isRequestSent() {
        return this.requestSent;
    }


    public String getReason() {
        return this.reason;
    }


    public String getSearched() {
        return this.searched;
    }
}


