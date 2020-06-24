package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketPlayPlayerOnline extends LabyPacket {

    private ChatUser newOnlinePlayer;

    public LabyPacketPlayPlayerOnline(ChatUser newOnlinePlayer) {
        this.newOnlinePlayer = newOnlinePlayer;
    }


    public LabyPacketPlayPlayerOnline() {
    }


    public void read(ProtoBuf buf) {
        this.newOnlinePlayer = LabyBufferUtils.readChatUser(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.newOnlinePlayer);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public ChatUser getPlayer() {
        return this.newOnlinePlayer;
    }
}


