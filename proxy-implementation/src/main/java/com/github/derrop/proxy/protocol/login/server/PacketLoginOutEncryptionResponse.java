package com.github.derrop.proxy.protocol.login.server;

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
public class PacketLoginOutEncryptionResponse extends DefinedPacket {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    @Override
    public void read(@NotNull ByteBuf buf) {
        sharedSecret = readArray(buf, 128);
        verifyToken = readArray(buf, 128);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeArray(sharedSecret, buf);
        writeArray(verifyToken, buf);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.ENCRYPTION_BEGIN;
    }
}
