package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPluginMessage;

public class PacketPlayClientCustomPayload extends PacketPlayServerPluginMessage {

    public PacketPlayClientCustomPayload(String tag, byte[] data) {
        super(tag, data);
    }

    public PacketPlayClientCustomPayload() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.CUSTOM_PAYLOAD;
    }

    public String toString() {
        return "PacketPlayClientCustomPayload()";
    }
}
