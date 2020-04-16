package com.github.derrop.proxy.protocol.play.server.entity.player;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerGameStateChange implements Packet {

    private int state;
    private float value;

    public PacketPlayServerGameStateChange(int state, float value) {
        this.state = state;
        this.value = value;
    }

    public PacketPlayServerGameStateChange() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.GAME_STATE_CHANGE;
    }

    public int getState() {
        return this.state;
    }

    public float getValue() {
        return this.value;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.state = protoBuf.readUnsignedByte();
        this.value = protoBuf.readFloat();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeByte(this.state);
        protoBuf.writeFloat(this.value);
    }

    public String toString() {
        return "PacketPlayServerGameStateChange(state=" + this.getState() + ", value=" + this.getValue() + ")";
    }
}
