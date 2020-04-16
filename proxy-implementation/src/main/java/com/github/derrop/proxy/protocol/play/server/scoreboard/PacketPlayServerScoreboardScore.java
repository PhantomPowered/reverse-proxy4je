package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardScore implements Packet {

    private String itemName;
    private byte action;
    private String objectiveName;
    private int value;

    public PacketPlayServerScoreboardScore(String itemName, String objectiveName) {
        this(itemName, (byte) 1, objectiveName, -1);
    }

    public PacketPlayServerScoreboardScore(String itemName, byte action, String objectiveName, int value) {
        this.itemName = itemName;
        this.action = action;
        this.objectiveName = objectiveName;
        this.value = value;
    }

    public PacketPlayServerScoreboardScore() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_SCORE;
    }

    public String getItemName() {
        return this.itemName;
    }

    public byte getAction() {
        return this.action;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public int getValue() {
        return this.value;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.itemName = protoBuf.readString();
        this.action = protoBuf.readByte();
        this.objectiveName = protoBuf.readString();

        if (action != 1) {
            this.value = protoBuf.readVarInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.itemName);
        protoBuf.writeByte(this.action);
        protoBuf.writeString(this.objectiveName);

        if (action != 1) {
            protoBuf.writeVarInt(this.value);
        }
    }

    public String toString() {
        return "PacketPlayServerScoreboardScore(itemName=" + this.getItemName() + ", action=" + this.getAction() + ", objectiveName=" + this.getObjectiveName() + ", value=" + this.getValue() + ")";
    }
}
