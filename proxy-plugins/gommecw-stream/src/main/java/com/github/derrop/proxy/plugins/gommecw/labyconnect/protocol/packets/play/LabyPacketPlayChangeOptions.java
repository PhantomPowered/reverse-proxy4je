package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login.LabyPacketLoginOptions;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserStatus;

import java.util.TimeZone;

public class LabyPacketPlayChangeOptions extends LabyPacket {
    private LabyPacketLoginOptions.Options options;

    public LabyPacketPlayChangeOptions(LabyPacketLoginOptions.Options options) {
        this.options = options;
    }


    public LabyPacketPlayChangeOptions(boolean showServer, UserStatus status, TimeZone timeZone) {
        this.options = new LabyPacketLoginOptions.Options(showServer, status, timeZone);
    }


    public LabyPacketPlayChangeOptions() {
    }


    public void read(ProtoBuf buf) {
        this.options = new LabyPacketLoginOptions.Options(buf.readBoolean(), LabyBufferUtils.readUserStatus(buf), TimeZone.getTimeZone(LabyBufferUtils.readString(buf)));
    }


    public void write(ProtoBuf buf) {
        buf.writeBoolean(getOptions().isShowServer());
        LabyBufferUtils.writeUserStatus(buf, getOptions().getOnlineStatus());
        LabyBufferUtils.writeString(buf, getOptions().getTimeZone().getID());
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public LabyPacketLoginOptions.Options getOptions() {
        return this.options;
    }
}


