package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerChatMessage;

public class PacketPlayClientChatMessage extends PacketPlayServerChatMessage {

    public PacketPlayClientChatMessage(String message, byte position) {
        super(message, position);
    }

    public PacketPlayClientChatMessage(String message) {
        super(message);
    }

    public PacketPlayClientChatMessage() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.CHAT;
    }

    public String toString() {
        return "PacketPlayClientChatMessage()";
    }
}
