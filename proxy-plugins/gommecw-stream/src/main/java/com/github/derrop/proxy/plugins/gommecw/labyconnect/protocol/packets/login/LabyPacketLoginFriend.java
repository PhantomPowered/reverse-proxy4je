package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class LabyPacketLoginFriend
        extends LabyPacket {
    private List<ChatUser> friends;

    public LabyPacketLoginFriend(List<ChatUser> friends) {
        this.friends = friends;
    }

    public LabyPacketLoginFriend() {
    }


    public void read(ProtoBuf buf) {
        List<ChatUser> players = new ArrayList<>();
        int a = buf.readInt();
        for (int i = 0; i < a; i++) {
            players.add(LabyBufferUtils.readChatUser(buf));
        }
        this.friends = new ArrayList<>();
        this.friends.addAll(players);
    }


    public void write(ProtoBuf buf) {
        buf.writeInt(getFriends().size());
        for (int i = 0; i < getFriends().size(); i++) {

            ChatUser p = getFriends().get(i);
            LabyBufferUtils.writeChatUser(buf, p);
        }
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public List<ChatUser> getFriends() {
        return this.friends;
    }
}


