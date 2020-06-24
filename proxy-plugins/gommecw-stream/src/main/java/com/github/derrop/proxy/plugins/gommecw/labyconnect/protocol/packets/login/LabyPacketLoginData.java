package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;

import java.util.UUID;


public class LabyPacketLoginData
        extends LabyPacket {
    private UUID id;
    private String name;
    private String motd;

    public LabyPacketLoginData() {
    }

    public LabyPacketLoginData(UUID id, String name, String motd) {
        this.id = id;
        this.name = name;
        this.motd = motd;
    }


    public void read(ProtoBuf buf) {
        this.id = UUID.fromString(LabyBufferUtils.readString(buf));
        this.name = LabyBufferUtils.readString(buf);
        this.motd = LabyBufferUtils.readString(buf);
    }


    public void write(ProtoBuf buf) {
        if (this.id == null) {
            LabyBufferUtils.writeString(buf, UUID.randomUUID().toString());
        } else {
            LabyBufferUtils.writeString(buf, this.id.toString());
        }
        LabyBufferUtils.writeString(buf, this.name);
        LabyBufferUtils.writeString(buf, this.motd);
    }


    public int getId() {
        return 0;
    }


    public UUID getUUID() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public String getMotd() {
        return this.motd;
    }
}


