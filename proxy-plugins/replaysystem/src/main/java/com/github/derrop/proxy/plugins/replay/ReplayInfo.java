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

import com.github.derrop.proxy.api.network.NetworkAddress;
import com.github.derrop.proxy.api.session.MCServiceCredentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ReplayInfo {

    private NetworkAddress serverAddress;
    private UUID creatorId;
    private String creatorName;
    private MCServiceCredentials recorder;
    private long timestamp;
    private int ownEntityId;

    public ReplayInfo(NetworkAddress serverAddress, UUID creatorId, String creatorName, MCServiceCredentials recorder, long timestamp, int ownEntityId) {
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

    public MCServiceCredentials getRecorder() {
        return recorder;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.ownEntityId);

        outputStream.writeUTF(this.serverAddress.getRawHost());
        outputStream.writeUTF(this.serverAddress.getHost());
        outputStream.writeInt(this.serverAddress.getPort());

        outputStream.writeLong(this.creatorId.getMostSignificantBits());
        outputStream.writeLong(this.creatorId.getLeastSignificantBits());

        outputStream.writeUTF(this.creatorName);

        outputStream.writeBoolean(this.recorder.isOffline());
        if (this.recorder.isOffline()) {
            outputStream.writeUTF(this.recorder.getUsername());
            outputStream.writeUTF(this.recorder.getDefaultServer());
            outputStream.writeBoolean(this.recorder.isExportable());
        } else {
            outputStream.writeUTF(this.recorder.getEmail());
            outputStream.writeUTF(this.recorder.getPassword());
            outputStream.writeUTF(this.recorder.getDefaultServer());
            outputStream.writeBoolean(this.recorder.isExportable());
        }

        outputStream.writeLong(this.timestamp);
    }

    public void read(DataInputStream inputStream) throws IOException {
        this.ownEntityId = inputStream.readInt();

        this.serverAddress = new NetworkAddress(inputStream.readUTF(), inputStream.readUTF(), inputStream.readInt());

        this.creatorId = new UUID(inputStream.readLong(), inputStream.readLong());
        this.creatorName = inputStream.readUTF();

        boolean offline = inputStream.readBoolean();
        if (offline) {
            this.recorder = MCServiceCredentials.online(inputStream.readUTF(), inputStream.readUTF(), inputStream.readUTF(), inputStream.readBoolean());
        } else {
            this.recorder = MCServiceCredentials.offline(inputStream.readUTF(), inputStream.readUTF(), inputStream.readBoolean());
        }

        this.timestamp = inputStream.readLong();
    }

}
