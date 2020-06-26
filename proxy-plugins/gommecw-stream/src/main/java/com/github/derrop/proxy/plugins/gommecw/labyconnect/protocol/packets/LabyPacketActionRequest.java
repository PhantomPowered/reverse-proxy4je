package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.util.UUID;


public class LabyPacketActionRequest
        extends LabyPacket {
    private UUID uuid;

    public LabyPacketActionRequest() {
    }

    public LabyPacketActionRequest(UUID uuid) {
        this.uuid = uuid;
    }


    public void read(ProtoBuf buf) {
        this.uuid = UUID.fromString(LabyBufferUtils.readString(buf));
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.uuid.toString());
    }


    public void handle(PacketHandler handler) {
    }


    public UUID getUuid() {
        return this.uuid;
    }
}


