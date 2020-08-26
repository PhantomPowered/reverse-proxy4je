package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientCommand implements Packet {

    private Action action;

    public PacketPlayClientCommand(Action action) {
        this.action = action;
    }

    public PacketPlayClientCommand() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.action = Action.values()[buf.readVarInt()];
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeVarInt(this.action.ordinal());
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.CLIENT_COMMAND;
    }

    @Override
    public String toString() {
        return "PacketPlayClientCommand{"
                + "action=" + action
                + '}';
    }

    public enum Action {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY,
        ;
    }

}
