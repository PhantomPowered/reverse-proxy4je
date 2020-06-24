package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketPlayFriendPlayingOn extends LabyPacket {
    private ChatUser player;
    private String gameModeName;

    public LabyPacketPlayFriendPlayingOn(ChatUser player, String gameModeName) {
        this.player = player;
        this.gameModeName = gameModeName;
    }


    public LabyPacketPlayFriendPlayingOn() {
    }


    public void read(ProtoBuf buf) {
        this.player = LabyBufferUtils.readChatUser(buf);
        this.gameModeName = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.player);
        LabyBufferUtils.writeString(buf, this.gameModeName);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getGameModeName() {
        return this.gameModeName;
    }


    public ChatUser getPlayer() {
        return this.player;
    }
}


