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
package com.github.derrop.proxy.plugins.replay;

import com.google.common.base.Preconditions;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReplayInputStream extends DataInputStream {
    private final ReplayInfo replayInfo;
    private boolean closed = false;

    public ReplayInputStream(InputStream in) throws IOException {
        super(in);

        this.replayInfo = new ReplayInfo();
        this.replayInfo.read(this);
    }

    public ReplayInfo getReplayInfo() {
        return this.replayInfo;
    }

    public ReplayPacket readPacket() throws IOException {
        if (this.available() <= 0) {
            this.close();
            return null;
        }

        long timestamp = super.readLong();
        int length = super.readInt();
        byte[] data = new byte[length];
        Preconditions.checkArgument(super.read(data) == length, "Missing ending of the packet");
        return new ReplayPacket(data, timestamp);
    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}
