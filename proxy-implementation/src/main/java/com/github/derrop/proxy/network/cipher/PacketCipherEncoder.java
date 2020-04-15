package com.github.derrop.proxy.network.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

public final class PacketCipherEncoder extends MessageToByteEncoder<ByteBuf> {

    private final PacketCipherHandler packetCipherHandler;

    public PacketCipherEncoder(@NotNull SecretKey secretKey) throws GeneralSecurityException {
        this.packetCipherHandler = new PacketCipherHandler(true, secretKey);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        this.packetCipherHandler.cipher(byteBuf, byteBuf2);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.packetCipherHandler.end();
    }
}
