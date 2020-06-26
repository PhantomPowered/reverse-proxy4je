package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;


import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

public class LabyPacketLoginTime
        extends LabyPacket {
    private ChatUser player;
    private long dateJoined;
    private long lastOnline;

    public LabyPacketLoginTime(ChatUser player, long dateJoined, long lastOnline) {
        this.player = player;
        this.dateJoined = dateJoined;
        this.lastOnline = lastOnline;
    }


    public LabyPacketLoginTime() {
    }


    public void read(ProtoBuf buf) {
        this.player = LabyBufferUtils.readChatUser(buf);
        this.dateJoined = buf.readLong();
        this.lastOnline = buf.readLong();
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeChatUser(buf, this.player);
        buf.writeLong(this.dateJoined);
        buf.writeLong(this.lastOnline);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public long getDateJoined() {
        return this.dateJoined;
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public ChatUser getPlayer() {
        return this.player;
    }
}


