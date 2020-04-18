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
package com.github.derrop.proxy.api.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ByteBufUtils {

    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public static void writeArray(byte[] b, ByteBuf buf) {
        if (b.length > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format("Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", b.length));
        }
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeArrayNoLimit(byte[] b, ByteBuf buf) {
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);

        return ret;
    }

    public static byte[] readArray(ByteBuf buf) {
        return readArray(buf, buf.readableBytes());
    }

    public static byte[] readArray(ByteBuf buf, int limit) {
        int len = readVarInt(buf);
        if (len > limit) {
            throw new OverflowPacketException(String.format("Cannot receive byte array longer than %s (got %s bytes)", limit, len));
        }
        byte[] ret = new byte[len];
        buf.readBytes(ret);
        return ret;
    }

    public static int[] readVarIntArray(ByteBuf buf) {
        int len = readVarInt(buf);
        int[] ret = new int[len];

        for (int i = 0; i < len; i++) {
            ret[i] = readVarInt(buf);
        }

        return ret;
    }

    public static void writeStringArray(List<String> s, ByteBuf buf) {
        writeVarInt(s.size(), buf);
        for (String str : s) {
            writeString(str, buf);
        }
    }

    public static List<String> readStringArray(ByteBuf buf) {
        int len = readVarInt(buf);
        List<String> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            ret.add(readString(buf));
        }
        return ret;
    }

    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static long readVarLong(ByteBuf buf) {
        long i = 0L;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (long) (b0 & 127) << j++ * 7;

            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public static void writeVarLong(long value, ByteBuf buf) {
        while ((value & -128L) != 0L) {
            buf.writeByte((int) (value & 127L) | 128);
            value >>>= 7;
        }

        buf.writeByte((int) value);
    }

    public static int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }

    public static void writeUUID(UUID value, ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf input) {
        return new UUID(input.readLong(), input.readLong());
    }

}
