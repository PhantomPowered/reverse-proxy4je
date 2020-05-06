package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerWorldSound implements Packet {

    private int soundType;
    private BlockPos soundPos;

    private int soundData;
    private boolean serverWide;

    public PacketPlayServerWorldSound(int soundType, BlockPos soundPos, int soundData, boolean serverWide) {
        this.soundType = soundType;
        this.soundPos = soundPos;
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

    public BlockPos getSoundPos() {
        return soundPos;
    }

    public void setSoundPos(BlockPos soundPos) {
        this.soundPos = soundPos;
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
        this.soundPos = protoBuf.readBlockPos();
        this.soundData = protoBuf.readInt();
        this.serverWide = protoBuf.readBoolean();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeInt(this.soundType);
        protoBuf.writeBlockPos(this.soundPos);
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
                ", soundPos=" + soundPos +
                ", soundData=" + soundData +
                ", serverWide=" + serverWide +
                '}';
    }
}
