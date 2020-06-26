package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketEncryptionRequest
        extends LabyPacket {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public LabyPacketEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }


    public LabyPacketEncryptionRequest() {
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }


    public String getServerId() {
        return this.serverId;
    }


    public byte[] getVerifyToken() {
        return this.verifyToken;
    }


    public void read(ProtoBuf buf) {
        this.serverId = LabyBufferUtils.readString(buf);
        this.publicKey = LabyBufferUtils.readArray(buf);
        this.verifyToken = LabyBufferUtils.readArray(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.serverId);
        LabyBufferUtils.writeArray(buf, this.publicKey);
        LabyBufferUtils.writeArray(buf, this.verifyToken);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


