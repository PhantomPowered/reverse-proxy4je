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


    public static final SimpleChannelInitializer BASE = new SimpleChannelInitializer();

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
