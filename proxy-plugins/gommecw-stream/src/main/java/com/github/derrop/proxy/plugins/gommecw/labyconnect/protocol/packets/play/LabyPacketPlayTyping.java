package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketPlayTyping extends LabyPacket {
    private ChatUser player;
    private ChatUser inChatWith;
    private boolean typing;

    public LabyPacketPlayTyping(ChatUser player, ChatUser inChatWith, boolean typing) {
        this.player = player;
        this.inChatWith = inChatWith;
        this.typing = typing;
    }


    public LabyPacketPlayTyping() {
    }


    public void read(ProtoBuf buf) {
        this.player = LabyBufferUtils.readChatUser(buf);
        this.inChatWith = LabyBufferUtils.readChatUser(buf);
        this.typing = buf.readBoolean();
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.player);
        LabyBufferUtils.writeChatUser(buf, this.inChatWith);
        buf.writeBoolean(this.typing);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public ChatUser getInChatWith() {
        return this.inChatWith;
    }


    public ChatUser getPlayer() {
        return this.player;
    }


    public boolean isTyping() {
        return this.typing;
    }
}


