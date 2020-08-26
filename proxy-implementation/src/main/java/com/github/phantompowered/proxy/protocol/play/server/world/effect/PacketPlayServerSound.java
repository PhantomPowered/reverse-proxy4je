package com.github.phantompowered.proxy.protocol.play.server.world.effect;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSound implements Packet {

    private String soundName;
    private int posX;
    private int posY;
    private int posZ;
    private float soundVolume;
    private int soundPitch;

    public PacketPlayServerSound(String soundName, double soundX, double soundY, double soundZ, float volume, float pitch) {
        this.soundName = soundName;
        this.posX = (int) (soundX * 8.0D);
        this.posY = (int) (soundY * 8.0D);
        this.posZ = (int) (soundZ * 8.0D);
        this.soundVolume = volume;
        this.soundPitch = (int) (pitch * 63.0F);
    }

    public PacketPlayServerSound() {
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    public int getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(int soundPitch) {
        this.soundPitch = soundPitch;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.soundName = protoBuf.readString();
        this.posX = protoBuf.readInt();
        this.posY = protoBuf.readInt();
        this.posZ = protoBuf.readInt();
        this.soundVolume = protoBuf.readFloat();
        this.soundPitch = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.soundName);
        protoBuf.writeInt(this.posX);
        protoBuf.writeInt(this.posY);
        protoBuf.writeInt(this.posZ);
        protoBuf.writeFloat(this.soundVolume);
        protoBuf.writeByte(this.soundPitch);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.NAMED_SOUND_EFFECT;
    }

    @Override
    public String toString() {
        return "PacketPlayServerSound{"
                + "soundName='" + soundName + '\''
                + ", posX=" + posX
                + ", posY=" + posY
                + ", posZ=" + posZ
                + ", soundVolume=" + soundVolume
                + ", soundPitch=" + soundPitch
                + '}';
    }
}
