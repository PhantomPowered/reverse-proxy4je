package com.github.derrop.proxy.plugins.replay;

import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ReplayInfo {

    private NetworkAddress serverAddress;
    private UUID creatorId;
    private String creatorName;
    private MCCredentials recorder;
    private long timestamp;
    private int ownEntityId;

    public ReplayInfo(NetworkAddress serverAddress, UUID creatorId, String creatorName, MCCredentials recorder, long timestamp, int ownEntityId) {
        this.serverAddress = serverAddress;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.recorder = recorder;
        this.timestamp = timestamp;
        this.ownEntityId = ownEntityId;
    }

    public ReplayInfo() {
    }

    public int getOwnEntityId() {
        return this.ownEntityId;
    }

    public NetworkAddress getServerAddress() {
        return serverAddress;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public MCCredentials getRecorder() {
        return recorder;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.ownEntityId);

        outputStream.writeUTF(this.serverAddress.getHost());
        outputStream.writeInt(this.serverAddress.getPort());

        outputStream.writeLong(this.creatorId.getMostSignificantBits());
        outputStream.writeLong(this.creatorId.getLeastSignificantBits());

        outputStream.writeUTF(this.creatorName);

        outputStream.writeBoolean(this.recorder.isOffline());
        if (this.recorder.isOffline()) {
            outputStream.writeUTF(this.recorder.getUsername());
        } else {
            outputStream.writeUTF(this.recorder.getEmail());
            outputStream.writeUTF(this.recorder.getPassword());
        }

        outputStream.writeLong(this.timestamp);
    }
    
    public void read(DataInputStream inputStream) throws IOException {
        this.ownEntityId = inputStream.readInt();

        this.serverAddress = new NetworkAddress(inputStream.readUTF(), inputStream.readInt());

        this.creatorId = new UUID(inputStream.readLong(), inputStream.readLong());
        this.creatorName = inputStream.readUTF();

        boolean offline = inputStream.readBoolean();
        if (offline) {
            this.recorder = new MCCredentials(inputStream.readUTF());
        } else {
            this.recorder = new MCCredentials(inputStream.readUTF(), inputStream.readUTF());
        }

        this.timestamp = inputStream.readLong();
    }
    
}
