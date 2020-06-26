package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketLoginVersion
        extends LabyPacket {
    private int versionId;
    private String versionName;
    private String updateLink;

    public LabyPacketLoginVersion(int internalVersion, String externalVersion) {
        this.versionId = internalVersion;
        this.versionName = externalVersion;
    }


    public LabyPacketLoginVersion() {
    }


    public void read(ProtoBuf buf) {
        this.versionId = buf.readInt();
        this.versionName = LabyBufferUtils.readString(buf);
        this.updateLink = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        buf.writeInt(this.versionId);
        LabyBufferUtils.writeString(buf, this.versionName);
        LabyBufferUtils.writeString(buf, "");
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public String getVersionName() {
        return this.versionName;
    }

    public int getVersionID() {
        return this.versionId;
    }

    public String getUpdateLink() {
        return this.updateLink;
    }
}


