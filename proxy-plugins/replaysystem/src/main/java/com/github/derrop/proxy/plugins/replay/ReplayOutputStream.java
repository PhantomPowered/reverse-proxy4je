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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReplayOutputStream extends DataOutputStream {

    private final BlockingQueue<ReplayPacket> receivedPackets = new LinkedBlockingQueue<>();

    private boolean closed = false;
    private Runnable closeHandler;

    public ReplayOutputStream(ReplayInfo replayInfo, OutputStream outputStream) throws IOException {
        this(replayInfo, Executors.newFixedThreadPool(1), outputStream);
    }

    public ReplayOutputStream(ReplayInfo replayInfo, ExecutorService executorService, OutputStream outputStream) throws IOException {
        super(outputStream);

        replayInfo.write(this);

        executorService.execute(() -> {
            while (!this.closed) {
                try {
                    ReplayPacket packet = this.receivedPackets.take();

                    this.writeLong(packet.getTimestamp());
                    this.writeInt(packet.getData().length);
                    this.write(packet.getData());
                } catch (InterruptedException | IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void write(ReplayPacket packet) {
        Preconditions.checkArgument(!this.closed, "stream already closed");
        this.receivedPackets.offer(packet);
    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.closed = true;
        if (this.closeHandler != null) {
            this.closeHandler.run();
        }
    }

    public void setCloseHandler(Runnable closeHandler) {
        this.closeHandler = closeHandler;
    }
}
