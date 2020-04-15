package com.github.derrop.proxy.network.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.util.List;

public final class PacketCipherDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final PacketCipherHandler packetCipherHandler;

    public PacketCipherDecoder(@NotNull SecretKey secretKey) throws GeneralSecurityException {
        this.packetCipherHandler = new PacketCipherHandler(false, secretKey);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(this.packetCipherHandler.cipher(channelHandlerContext, byteBuf));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.packetCipherHandler.end();
    }
}
