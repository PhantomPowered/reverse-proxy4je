package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientSettings implements Packet {

    private String locale;
    private byte viewDistance;
    private int chatFlags;
    private boolean chatColours;
    private byte difficulty;
    private byte skinParts;

    public PacketPlayClientSettings(String locale, byte viewDistance, int chatFlags, boolean chatColours, byte difficulty, byte skinParts) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColours = chatColours;
        this.difficulty = difficulty;
        this.skinParts = skinParts;
    }

    public PacketPlayClientSettings() {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.SETTINGS;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.locale = protoBuf.readString();
        this.viewDistance = protoBuf.readByte();
        this.chatFlags = protoBuf.readUnsignedByte();
        this.chatColours = protoBuf.readBoolean();
        this.skinParts = protoBuf.readByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.locale);
        protoBuf.writeByte(this.viewDistance);
        protoBuf.writeByte(this.chatFlags);
        protoBuf.writeBoolean(this.chatColours);
        protoBuf.writeByte(this.skinParts);
    }

    public String getLocale() {
        return this.locale;
    }

    public byte getViewDistance() {
        return this.viewDistance;
    }

    public int getChatFlags() {
        return this.chatFlags;
    }

    public boolean isChatColours() {
        return this.chatColours;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public byte getSkinParts() {
        return this.skinParts;
    }

    public String toString() {
        return "PacketPlayClientSettings(locale=" + this.getLocale() + ", viewDistance=" + this.getViewDistance() + ", chatFlags=" + this.getChatFlags() + ", chatColours=" + this.isChatColours() + ", difficulty=" + this.getDifficulty() + ", skinParts=" + this.getSkinParts() + ")";
    }
}
