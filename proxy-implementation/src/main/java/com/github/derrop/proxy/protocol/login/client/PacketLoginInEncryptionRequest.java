package com.github.derrop.proxy.protocol.login.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginInEncryptionRequest implements Packet {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public PacketLoginInEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public PacketLoginInEncryptionRequest() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.ENCRYPTION_BEGIN;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.serverId = protoBuf.readString();
        this.publicKey = protoBuf.readArray();
        this.verifyToken = protoBuf.readArray();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.serverId);
        protoBuf.writeArray(this.publicKey);
        protoBuf.writeArray(this.verifyToken);
    }

    public String getServerId() {
        return this.serverId;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public String toString() {
        return "PacketLoginInEncryptionRequest(serverId=" + this.getServerId() + ", publicKey=" + java.util.Arrays.toString(this.getPublicKey()) + ", verifyToken=" + java.util.Arrays.toString(this.getVerifyToken()) + ")";
    }

}
