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
package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.util.OverflowPacketException;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class ByteBufUtils {

    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            // TODO - why
            throw new OverflowPacketException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            // TODO - why
            throw new OverflowPacketException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public static byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);

        return ret;
    }

    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        do {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

        } while ((in & 0x80) == 0x80);

        return out;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        do {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);
        } while (value != 0);
    }

}
