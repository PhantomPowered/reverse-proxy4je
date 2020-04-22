package com.github.derrop.proxy.protocol.play.server.world.effect;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PacketPlayServerExplosion implements Packet {

    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private List<BlockPos> affectedBlockPositions;
    private float velocityX;
    private float velocityY;
    private float velocityZ;

    public PacketPlayServerExplosion(double posX, double posY, double posZ, float strength, List<BlockPos> affectedBlockPositions, float velocityX, float velocityY, float velocityZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.strength = strength;
        this.affectedBlockPositions = affectedBlockPositions;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    public PacketPlayServerExplosion() {
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return affectedBlockPositions;
    }

    public void setAffectedBlockPositions(List<BlockPos> affectedBlockPositions) {
        this.affectedBlockPositions = affectedBlockPositions;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(float velocityZ) {
        this.velocityZ = velocityZ;
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.strength = buf.readFloat();
        int i = buf.readInt();
        this.affectedBlockPositions = Lists.<BlockPos>newArrayListWithCapacity(i);
        int j = (int) this.posX;
        int k = (int) this.posY;
        int l = (int) this.posZ;

        for (int i1 = 0; i1 < i; ++i1) {
            int j1 = buf.readByte() + j;
            int k1 = buf.readByte() + k;
            int l1 = buf.readByte() + l;
            this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
        }

        this.velocityX = buf.readFloat();
        this.velocityY = buf.readFloat();
        this.velocityZ = buf.readFloat();
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeFloat((float) this.posX);
        buf.writeFloat((float) this.posY);
        buf.writeFloat((float) this.posZ);
        buf.writeFloat(this.strength);
        buf.writeInt(this.affectedBlockPositions.size());
        int i = (int) this.posX;
        int j = (int) this.posY;
        int k = (int) this.posZ;

        for (BlockPos blockpos : this.affectedBlockPositions) {
            int l = blockpos.getX() - i;
            int i1 = blockpos.getY() - j;
            int j1 = blockpos.getZ() - k;
            buf.writeByte(l);
            buf.writeByte(i1);
            buf.writeByte(j1);
        }

        buf.writeFloat(this.velocityX);
        buf.writeFloat(this.velocityY);
        buf.writeFloat(this.velocityZ);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.EXPLOSION;
    }
}
