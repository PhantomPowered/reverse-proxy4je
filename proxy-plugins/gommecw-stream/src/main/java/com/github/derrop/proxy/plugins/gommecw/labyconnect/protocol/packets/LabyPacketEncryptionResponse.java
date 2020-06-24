package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.CryptManager;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import javax.crypto.SecretKey;
import java.security.PublicKey;


public class LabyPacketEncryptionResponse
        extends LabyPacket {
    private byte[] sharedSecret;
    private byte[] verifyToken;

    public LabyPacketEncryptionResponse(SecretKey key, PublicKey publicKey, byte[] hash) {
        this.sharedSecret = CryptManager.encryptData(publicKey, key.getEncoded());
        this.verifyToken = CryptManager.encryptData(publicKey, hash);
    }


    public LabyPacketEncryptionResponse() {
    }

    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }


    public byte[] getVerifyToken() {
        return this.verifyToken;
    }


    public void read(ProtoBuf buf) {
        this.sharedSecret = LabyBufferUtils.readArray(buf);
        this.verifyToken = LabyBufferUtils.readArray(buf);
    }


    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeArray(buf, this.sharedSecret);
        LabyBufferUtils.writeArray(buf, this.verifyToken);
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


