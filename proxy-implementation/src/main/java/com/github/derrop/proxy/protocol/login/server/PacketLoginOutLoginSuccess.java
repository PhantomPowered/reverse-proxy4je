package com.github.derrop.proxy.protocol.login.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketLoginOutLoginSuccess implements Packet {

    private String uniqueId;
    private String username;

    public PacketLoginOutLoginSuccess(String uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
    }

    public PacketLoginOutLoginSuccess() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Login.SUCCESS;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.uniqueId = protoBuf.readString();
        this.username = protoBuf.readString();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.uniqueId);
        protoBuf.writeString(this.username);
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String toString() {
        return "PacketLoginOutLoginSuccess(uuid=" + this.getUniqueId() + ", username=" + this.getUsername() + ")";
    }
}
