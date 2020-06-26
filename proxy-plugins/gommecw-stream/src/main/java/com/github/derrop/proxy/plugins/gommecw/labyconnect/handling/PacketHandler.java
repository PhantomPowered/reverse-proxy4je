package com.github.derrop.proxy.plugins.gommecw.labyconnect.handling;

import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPing;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPong;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class PacketHandler
        extends SimpleChannelInboundHandler<Object> {
    protected void channelRead0(ChannelHandlerContext ctx, Object packet) throws Exception {
        handlePacket((LabyPacket) packet);
    }

    private void handlePacket(LabyPacket labyPacket) {
        labyPacket.handle(this);
    }

    public void handle(LabyPacketLoginData paramPacketLoginData) {
        
    }

    public void handle(LabyPacketHelloPing paramPacketHelloPing) {
        
    }

    public void handle(LabyPacketHelloPong paramPacketHelloPong) {
        
    }

    public void handle(LabyPacketPlayPlayerOnline paramPacketPlayPlayerOnline) {
        
    }

    public void handle(LabyPacketLoginComplete paramPacketLoginComplete) {
        
    }

    public void handle(LabyPacketChatVisibilityChange paramPacketChatVisibilityChange) {
        
    }

    public void handle(LabyPacketKick paramPacketKick) {
        
    }

    public void handle(LabyPacketDisconnect paramPacketDisconnect) {
        
    }

    public void handle(LabyPacketPlayRequestAddFriend paramPacketPlayRequestAddFriend) {
        
    }

    public void handle(LabyPacketLoginFriend paramPacketLoginFriend) {
        
    }

    public void handle(LabyPacketLoginRequest paramPacketLoginRequest) {
        
    }

    public void handle(LabyPacketBanned paramPacketBanned) {
        
    }

    public void handle(LabyPacketPing paramPacketPing) {
        
    }

    public void handle(LabyPacketPong paramPacketPong) {
        
    }

    public void handle(LabyPacketServerMessage paramPacketServerMessage) {
        
    }

    public void handle(LabyPacketMessage paramPacketMessage) {
        
    }

    public void handle(LabyPacketPlayTyping paramPacketPlayTyping) {
        
    }

    public void handle(LabyPacketPlayRequestAddFriendResponse paramPacketPlayRequestAddFriendResponse) {
        
    }

    public void handle(LabyPacketPlayRequestRemove paramPacketPlayRequestRemove) {
        
    }

    public void handle(LabyPacketPlayDenyFriendRequest paramPacketPlayDenyFriendRequest) {
        
    }

    public void handle(LabyPacketPlayFriendRemove paramPacketPlayFriendRemove) {
        
    }

    public void handle(LabyPacketLoginOptions paramPacketLoginOptions) {
        
    }

    public void handle(LabyPacketPlayServerStatus paramPacketPlayServerStatus) {
        
    }

    public void handle(LabyPacketPlayServerStatusUpdate paramPacketPlayServerStatusUpdate) {
        
    }

    public void handle(LabyPacketPlayFriendStatus paramPacketPlayFriendStatus) {
        
    }

    public void handle(LabyPacketPlayFriendPlayingOn paramPacketPlayFriendPlayingOn) {
        
    }

    public void handle(LabyPacketPlayChangeOptions paramPacketPlayChangeOptions) {
        
    }

    public void handle(LabyPacketLoginTime paramPacketLoginTime) {
        
    }

    public void handle(LabyPacketLoginVersion paramPacketLoginVersion) {
        
    }

    public void handle(LabyPacketEncryptionRequest paramPacketEncryptionRequest) {
        
    }

    public void handle(LabyPacketEncryptionResponse paramPacketEncryptionResponse) {
        
    }

    public void handle(LabyPacketMojangStatus paramPacketMojangStatus) {
        
    }

    public void handle(LabyPacketUpdateCosmetics paramPacketUpdateCosmetics) {
        
    }

    public void handle(LabyPacketAddonMessage paramPacketAddonMessage) {
        
    }

    public void handle(LabyPacketUserBadge paramPacketUserBadge) {
        
    }
}


