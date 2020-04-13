package com.github.derrop.proxy.plugins.replay;

public class ReplayPacket {

    private byte[] data;
    private long timestamp;

    public ReplayPacket(byte[] data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public byte[] getData() {
        return this.data;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
