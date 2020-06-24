package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.google.common.base.Objects;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ServerInfo;

public class LabyPacketPlayServerStatus
        extends LabyPacket {
    private String serverIp = "";
    private int port = 25565;
    private String gamemode = null;

    public LabyPacketPlayServerStatus(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = null;
    }

    public LabyPacketPlayServerStatus() {
    }

    public LabyPacketPlayServerStatus(String serverIp, int port, String gamemode) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = gamemode;
    }


    public void read(ProtoBuf buf) {
        this.serverIp = LabyBufferUtils.readString(buf);
        this.port = buf.readInt();
        if (buf.readBoolean()) {
            this.gamemode = LabyBufferUtils.readString(buf);
        }
    }

    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.serverIp);
        buf.writeInt(this.port);
        if (this.gamemode != null && !this.gamemode.isEmpty()) {
            buf.writeBoolean(true);
            LabyBufferUtils.writeString(buf, this.gamemode);
        } else {
            buf.writeBoolean(false);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public int getPort() {
        return this.port;
    }

    public String getGamemode() {
        return this.gamemode;
    }

    public ServerInfo build() {
        if (this.gamemode == null) {
            return new ServerInfo(this.serverIp, this.port);
        }
        return new ServerInfo(this.serverIp, this.port, this.gamemode);
    }

    public boolean equals(LabyPacketPlayServerStatus packet) {
        return (this.serverIp.equals(packet.getServerIp()) && this.port == packet.getPort() && Objects.equal(this.gamemode, packet.getGamemode()));
    }
}


