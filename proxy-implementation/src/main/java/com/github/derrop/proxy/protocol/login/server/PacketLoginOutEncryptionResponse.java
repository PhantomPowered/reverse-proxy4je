package com.github.derrop.proxy.protocol.login.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginOutEncryptionResponse implements Packet {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public PacketLoginOutEncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    public PacketLoginOutEncryptionResponse() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Login.ENCRYPTION_RESPONSE;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.sharedSecret = protoBuf.readArray(128);
        this.verifyToken = protoBuf.readArray(128);
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeArray(this.sharedSecret);
        protoBuf.writeArray(this.verifyToken);
    }

    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public String toString() {
        return "PacketLoginOutEncryptionResponse(sharedSecret=" + java.util.Arrays.toString(this.getSharedSecret()) + ", verifyToken=" + java.util.Arrays.toString(this.getVerifyToken()) + ")";
    }
}
