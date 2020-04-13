package com.github.derrop.proxy.plugins.replay;

import com.google.common.base.Preconditions;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReplayInputStream extends DataInputStream {
    private ReplayInfo replayInfo;
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
