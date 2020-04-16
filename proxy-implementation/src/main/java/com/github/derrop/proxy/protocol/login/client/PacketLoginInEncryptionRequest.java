package com.github.derrop.proxy.protocol.login.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketLoginInEncryptionRequest extends DefinedPacket {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    @Override
    public void read(@NotNull ByteBuf buf) {
        serverId = readString(buf);
        publicKey = readArray(buf);
        verifyToken = readArray(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(serverId, buf);
        writeArray(publicKey, buf);
        writeArray(verifyToken, buf);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.ENCRYPTION_BEGIN;
    }
}
