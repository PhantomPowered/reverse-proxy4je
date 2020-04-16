package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientResourcePackStatusResponse implements Packet {

    private String hash;
    private Action action;

    public PacketPlayClientResourcePackStatusResponse(String hash, Action action) {
        this.hash = hash;
        this.action = action;
    }

    public PacketPlayClientResourcePackStatusResponse() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.RESOURCE_PACK_STATUS;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.hash = protoBuf.readString();
        this.action = Action.values()[protoBuf.readVarInt()];
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.hash);
        protoBuf.writeVarInt(this.action.ordinal());
    }

    public String getHash() {
        return this.hash;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String toString() {
        return "PacketPlayClientResourcePackStatusResponse(hash=" + this.getHash() + ", action=" + this.getAction() + ")";
    }

    public enum Action {

        SUCCESSFULLY_LOADED,

        DECLINED,

        FAILED_DOWNLOAD,

        ACCEPTED,
        ;
    }

}
