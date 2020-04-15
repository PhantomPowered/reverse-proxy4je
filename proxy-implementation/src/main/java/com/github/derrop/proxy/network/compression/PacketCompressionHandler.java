package com.github.derrop.proxy.network.compression;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class PacketCompressionHandler {

    private final byte[] buffer = new byte[8192];

    private Inflater inflater;

    private Deflater deflater;

    private int threshold;

    PacketCompressionHandler(int threshold, boolean compressMode) {
        this.threshold = threshold;

        if (compressMode) {
            this.deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        } else {
            this.inflater = new Inflater();
        }
    }

    void end() {
        if (this.inflater != null) {
            this.inflater.end();
        }

        if (this.deflater != null) {
            this.deflater.end();
        }
    }

    void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    int getThreshold() {
        return threshold;
    }

    void process(@NotNull ByteBuf byteBuf, @NotNull ByteBuf byteBuf2) throws DataFormatException {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        if (this.deflater != null) {
            this.deflater.setInput(data);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                byteBuf2.writeBytes(this.buffer, 0, this.deflater.deflate(buffer));
            }

            this.deflater.reset();
        } else if (this.inflater != null) {
            this.inflater.setInput(data);

            while (!this.inflater.finished() && this.inflater.getTotalIn() < data.length) {
                byteBuf2.writeBytes(buffer, 0, this.inflater.inflate(buffer));
            }

            this.inflater.reset();
        } else {
            throw new RuntimeException("Unable to (de-) compress packet!");
        }
    }
}
