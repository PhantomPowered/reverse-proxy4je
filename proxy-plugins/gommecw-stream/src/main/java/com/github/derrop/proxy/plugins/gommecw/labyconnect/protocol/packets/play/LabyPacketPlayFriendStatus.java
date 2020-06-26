package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ServerInfo;

public class LabyPacketPlayFriendStatus extends LabyPacket {
    private ChatUser player;
    private ServerInfo playerInfo;

    public LabyPacketPlayFriendStatus(ChatUser player, ServerInfo playerInfo) {
        this.player = player;
        this.playerInfo = playerInfo;
    }


    public LabyPacketPlayFriendStatus() {
    }


    public void read(ProtoBuf buf) {
        this.player = LabyBufferUtils.readChatUser(buf);
        this.playerInfo = LabyBufferUtils.readServerInfo(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.player);
        LabyBufferUtils.writeServerInfo(buf, this.playerInfo);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public ChatUser getPlayer() {
        return this.player;
    }


    public ServerInfo getPlayerInfo() {
        return this.playerInfo;
    }
}


