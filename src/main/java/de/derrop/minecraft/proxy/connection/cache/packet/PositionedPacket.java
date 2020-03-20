package de.derrop.minecraft.proxy.connection.cache.packet;

public interface PositionedPacket {

    void setX(int x);

    void setY(int y);

    void setZ(int z);

    void setYaw(byte yaw);

    void setPitch(byte pitch);

    int getX();

    int getY();

    int getZ();

    byte getYaw();

    byte getPitch();


}
