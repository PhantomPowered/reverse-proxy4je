package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.google.common.base.Objects;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;


public class LabyPacketPlayServerStatusUpdate
        extends LabyPacket {
    private String serverIp = "";
    private int port = 25565;
    private String gamemode = null;
    private boolean viaServerlist;

    public LabyPacketPlayServerStatusUpdate(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = null;
    }

    public LabyPacketPlayServerStatusUpdate() {
    }

    public LabyPacketPlayServerStatusUpdate(String serverIp, int port, String gamemode, boolean viaServerlist) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = gamemode;
        this.viaServerlist = viaServerlist;
    }


    public void read(ProtoBuf buf) {
        this.serverIp = LabyBufferUtils.readString(buf);
        this.port = buf.readInt();
        this.viaServerlist = buf.readBoolean();
        if (buf.readBoolean()) {
            this.gamemode = LabyBufferUtils.readString(buf);
        }
    }

    public void write(ProtoBuf buf) {
        LabyBufferUtils.writeString(buf, this.serverIp);
        buf.writeInt(this.port);
        buf.writeBoolean(this.viaServerlist);
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

    public boolean equals(LabyPacketPlayServerStatusUpdate packet) {
        return (this.serverIp.equals(packet.serverIp) && this.port == packet.port && Objects.equal(this.gamemode, packet.gamemode));
    }
}


