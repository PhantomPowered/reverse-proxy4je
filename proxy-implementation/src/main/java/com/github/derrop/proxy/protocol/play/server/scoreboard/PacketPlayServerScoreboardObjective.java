package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.scoreboard.minecraft.criteria.IScoreObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardObjective implements Packet {

    private String name;
    private String value;
    private byte action;
    private IScoreObjectiveCriteria.EnumRenderType type;

    public PacketPlayServerScoreboardObjective(String name) {
        this(name, null, null, (byte) 1);
    }

    public PacketPlayServerScoreboardObjective(String name, String value, IScoreObjectiveCriteria.EnumRenderType type, byte action) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.action = action;
    }

    public PacketPlayServerScoreboardObjective() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.name = protoBuf.readString();
        this.action = protoBuf.readByte();

        if (action == 0 || action == 2) {
            this.value = protoBuf.readString();
            this.type = IScoreObjectiveCriteria.EnumRenderType.valueOf(protoBuf.readString().toUpperCase());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.name);
        protoBuf.writeByte(this.action);

        if (action == 0 || action == 2) {
            protoBuf.writeString(this.value);
            protoBuf.writeString(this.toString().toLowerCase());
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_OBJECTIVE;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public IScoreObjectiveCriteria.EnumRenderType getType() {
        return this.type;
    }

    public byte getAction() {
        return this.action;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(IScoreObjectiveCriteria.EnumRenderType type) {
        this.type = type;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public String toString() {
        return "PacketPlayServerScoreboardObjective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + this.getType() + ", action=" + this.getAction() + ")";
    }
}
