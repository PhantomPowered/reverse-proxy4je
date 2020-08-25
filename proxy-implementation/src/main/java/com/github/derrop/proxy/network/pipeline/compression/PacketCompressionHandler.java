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
package com.github.derrop.proxy.network.pipeline.compression;

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

    protected PacketCompressionHandler(int threshold, boolean compressMode) {
        this.threshold = threshold;

        if (compressMode) {
            this.deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        } else {
            this.inflater = new Inflater();
        }
    }

    protected void end() {
        if (this.inflater != null) {
            this.inflater.end();
        }

        if (this.deflater != null) {
            this.deflater.end();
        }
    }

    protected void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    protected int getThreshold() {
        return threshold;
    }

    protected void process(@NotNull ByteBuf byteBuf, @NotNull ByteBuf byteBuf2) throws DataFormatException {
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
