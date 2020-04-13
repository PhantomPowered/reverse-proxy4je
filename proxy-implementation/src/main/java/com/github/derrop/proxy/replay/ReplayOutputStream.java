package com.github.derrop.proxy.replay;

import com.google.common.base.Preconditions;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReplayOutputStream extends DataOutputStream {

    private BlockingQueue<ReplayPacket> receivedPackets = new LinkedBlockingQueue<>();

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
