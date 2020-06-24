package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketPlayFriendRemove extends LabyPacket {
    private ChatUser toRemove;

    public LabyPacketPlayFriendRemove(ChatUser toRemove) {
        this.toRemove = toRemove;
    }


    public LabyPacketPlayFriendRemove() {
    }


    public void read(ProtoBuf buf) {
        this.toRemove = LabyBufferUtils.readChatUser(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.toRemove);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public ChatUser getToRemove() {
        return this.toRemove;
    }
}


