package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWorldSound implements Packet {

    private int soundType;
    private Location soundLocation;

    private int soundData;
    private boolean serverWide;

    public PacketPlayServerWorldSound(int soundType, Location soundLocation, int soundData, boolean serverWide) {
        this.soundType = soundType;
        this.soundLocation = soundLocation;
        this.soundData = soundData;
        this.serverWide = serverWide;
    }

    public PacketPlayServerWorldSound() {
    }

    public int getSoundType() {
        return soundType;
    }

    public void setSoundType(int soundType) {
        this.soundType = soundType;
    }

    public Location getSoundLocation() {
        return soundLocation;
    }

    public void setSoundLocation(Location soundLocation) {
        this.soundLocation = soundLocation;
    }

    public int getSoundData() {
        return soundData;
    }

    public void setSoundData(int soundData) {
        this.soundData = soundData;
    }

    public boolean isServerWide() {
        return serverWide;
    }

    public void setServerWide(boolean serverWide) {
        this.serverWide = serverWide;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.soundType = protoBuf.readInt();
        this.soundLocation = protoBuf.readLocation();
        this.soundData = protoBuf.readInt();
        this.serverWide = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.soundType);
        protoBuf.writeLocation(this.soundLocation);
        protoBuf.writeInt(this.soundData);
        protoBuf.writeBoolean(this.serverWide);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WORLD_SOUND;
    }

    @Override
    public String toString() {
        return "PacketPlayServerWorldSound{" +
                "soundType=" + soundType +
                ", soundPos=" + soundLocation +
                ", soundData=" + soundData +
                ", serverWide=" + serverWide +
                '}';
    }
}
