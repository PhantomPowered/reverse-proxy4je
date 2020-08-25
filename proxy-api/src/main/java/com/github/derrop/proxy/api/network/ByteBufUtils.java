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

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ByteBufUtils {

    private static final RuntimeException BAD_VAR_INT_RECEIVED = new RuntimeException("Bad VarInt received");

    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            System.err.println("Unable to write string which is longer than the maximum allowed");
            return;
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            System.err.println("Unable to read string which is longer than the maximum allowed");
            return "{}";
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

    public static int readVarInt(@NotNull ByteBuf byteBuf) {
        Integer varInt = readVarIntUnchecked(byteBuf);
        if (varInt == null) {
            throw BAD_VAR_INT_RECEIVED;
        }

        return varInt;
    }

    public static @Nullable Integer readVarIntUnchecked(@NotNull ByteBuf byteBuf) {
        int i = 0;
        int maxRead = Math.min(5, byteBuf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = byteBuf.readByte();
            i |= (k & 127) << j * 7;
            if ((k & 128) != 128) {
                return i;
            }
        }

        return null;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        while (true) {
            if ((value & -128) == 0) {
                output.writeByte(value);
                return;
            }

            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }
    }
}
