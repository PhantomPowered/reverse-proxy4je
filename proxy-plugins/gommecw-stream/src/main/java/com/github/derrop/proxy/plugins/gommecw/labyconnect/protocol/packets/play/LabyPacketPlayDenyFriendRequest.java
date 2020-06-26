package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatRequest;

public class LabyPacketPlayDenyFriendRequest
        extends LabyPacket {
    private ChatRequest denied;

    public LabyPacketPlayDenyFriendRequest(ChatRequest denied) {
        this.denied = denied;
    }


    public LabyPacketPlayDenyFriendRequest() {
    }


    public void read(ProtoBuf buf) {
        this.denied = (ChatRequest) LabyBufferUtils.readChatUser(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.denied);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public ChatRequest getDenied() {
        return this.denied;
    }
}


