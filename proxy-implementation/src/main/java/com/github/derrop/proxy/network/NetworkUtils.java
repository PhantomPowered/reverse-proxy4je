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
package com.github.derrop.proxy.network;

import com.github.derrop.proxy.network.length.LengthFrameEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.codec.MessageToByteEncoder;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException();
    }


    public static final String TIMEOUT = "timeout";

    public static final String LENGTH_DECODER = "length_decoder";

    public static final String LENGTH_ENCODER = "length_encoder";

    public static final String ENDPOINT = "endpoint";

    public static final String PACKET_DECODER = "packet_decoder";

    public static final String PACKET_ENCODER = "packet_encoder";

    public static final String DECRYPT = "decrypt";

    public static final String ENCRYPT = "encrypt";

    public static final String COMPRESSOR = "compressor";

    public static final String DE_COMPRESSOR = "de-compressor";


    public static final MessageToByteEncoder<ByteBuf> LENGTH_FRAME_ENCODER = new LengthFrameEncoder();

    public static final WriteBufferWaterMark WATER_MARK = new WriteBufferWaterMark(524288, 2097152);

    public static int roundedPowDouble(double d1, double d2) {
        long result = Math.round(Math.pow(d1, d2));
        return longToInt(result);
    }

    public static int longToInt(long in) {
        return in > Integer.MAX_VALUE ? Integer.MAX_VALUE : in < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) in;
    }

    public static int varintSize(int paramInt) {
        if ((paramInt & 0xFFFFFF80) == 0) {
            return 1;
        }

        if ((paramInt & 0xFFFFC000) == 0) {
            return 2;
        }

        if ((paramInt & 0xFFE00000) == 0) {
            return 3;
        }

        if ((paramInt & 0xF0000000) == 0) {
            return 4;
        }

        return 5;
    }
}
