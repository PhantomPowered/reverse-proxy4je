package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketPlayServerTabCompleteResponse implements Packet {

    private List<String> commands;

    public PacketPlayServerTabCompleteResponse(List<String> commands) {
        this.commands = commands;
    }

    public PacketPlayServerTabCompleteResponse() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TAB_COMPLETE;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.commands = protoBuf.readStringArray();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeStringArray(this.commands);
    }

    @Override
    public String toString() {
        return "PacketPlayServerTabCompleteResponse{" + "commands=" + commands + '}';
    }
}
