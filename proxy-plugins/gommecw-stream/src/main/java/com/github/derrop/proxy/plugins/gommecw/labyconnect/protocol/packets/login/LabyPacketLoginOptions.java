package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.LabyBufferUtils;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.handling.PacketHandler;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.LabyPacket;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserStatus;

import java.util.TimeZone;

public class LabyPacketLoginOptions extends LabyPacket {
    private boolean showServer;
    private UserStatus status;
    private TimeZone timeZone;

    public LabyPacketLoginOptions(boolean showServer, UserStatus status, TimeZone timeZone) {
        this.showServer = showServer;
        this.status = status;
        this.timeZone = timeZone;
    }


    public LabyPacketLoginOptions() {
    }


    public void read(ProtoBuf buf) {
        this.showServer = buf.readBoolean();
        this.status = LabyBufferUtils.readUserStatus(buf);
        this.timeZone = TimeZone.getTimeZone(LabyBufferUtils.readString(buf));
    }


    public void write(ProtoBuf buf) {
        buf.writeBoolean(this.showServer);
        LabyBufferUtils.writeUserStatus(buf, this.status);
        LabyBufferUtils.writeString(buf, this.timeZone.getID());
    }


    public void handle(PacketHandler handler) {
        handler.handle(this);
    }


    public Options getOptions() {
        return new Options(this.showServer, this.status, this.timeZone);
    }


    public static class Options {
        private final boolean showServer;
        private final UserStatus onlineStatus;
        private final TimeZone timeZone;

        public Options(boolean showServer, UserStatus onlineStatus, TimeZone timeZone) {
            this.showServer = showServer;
            this.timeZone = timeZone;
            this.onlineStatus = onlineStatus;
        }


        public boolean isShowServer() {
            return this.showServer;
        }


        public UserStatus getOnlineStatus() {
            return this.onlineStatus;
        }

        public TimeZone getTimeZone() {
            return this.timeZone;
        }
    }
}


