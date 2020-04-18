/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.network.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;

public final class PacketCipherHandler {

    private final Cipher cipher;

    private final ThreadLocal<byte[]> inThreadLocal = ThreadLocal.withInitial(() -> new byte[0]);

    private final ThreadLocal<byte[]> outThreadLocal = ThreadLocal.withInitial(() -> new byte[0]);

    PacketCipherHandler(boolean encrypt, @NotNull SecretKey secretKey) throws GeneralSecurityException {
        this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(secretKey.getEncoded()));
    }

    void cipher(@NotNull ByteBuf byteBuf, @NotNull ByteBuf byteBuf2) throws ShortBufferException {
        int readable = byteBuf.readableBytes();
        byte[] inBytes = this.toByteArray(byteBuf);

        byte[] out = outThreadLocal.get();
        int outSize = this.cipher.getOutputSize(readable);

        if (out.length < outSize) {
            out = new byte[outSize];
            outThreadLocal.set(out);
        }

        byteBuf2.writeBytes(out, 0, this.cipher.update(inBytes, 0, readable, out));
    }

    @NotNull
    public ByteBuf cipher(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull ByteBuf byteBuf) throws ShortBufferException {
        int readable = byteBuf.readableBytes();
        byte[] inBytes = this.toByteArray(byteBuf);

        ByteBuf out = channelHandlerContext.alloc().heapBuffer(this.cipher.getOutputSize(readable));
        out.writerIndex(this.cipher.update(inBytes, 0, readable, out.array(), out.arrayOffset()));

        return out;
    }

    void end() {
        inThreadLocal.set(new byte[0]);
        outThreadLocal.set(new byte[0]);
    }

    @NotNull
    private byte[] toByteArray(@NotNull ByteBuf byteBuf) {
        byte[] heapIn = inThreadLocal.get();
        int readableBytes = byteBuf.readableBytes();
        if (heapIn.length < readableBytes) {
            heapIn = new byte[readableBytes];
            inThreadLocal.set(heapIn);
        }

        byteBuf.readBytes(heapIn, 0, readableBytes);
        return heapIn;
    }
}
