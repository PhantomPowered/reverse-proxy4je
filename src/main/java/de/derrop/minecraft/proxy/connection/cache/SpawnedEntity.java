package de.derrop.minecraft.proxy.connection.cache;

import de.derrop.minecraft.proxy.util.DataWatcher;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.UUID;

public class SpawnedEntity {

    private int entityId;
    private UUID playerId; // optional - only if this is a player
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private short currentItem; // optional - only if this is a player
    private List<DataWatcher.WatchableObject> watchableObjects;

    public SpawnedEntity(int entityId, UUID playerId, int x, int y, int z, byte yaw, byte pitch, short currentItem, List<DataWatcher.WatchableObject> watchableObjects) {
        this.entityId = entityId;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.currentItem = currentItem;
        this.watchableObjects = watchableObjects;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isPlayer() {
        return this.playerId != null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public byte getYaw() {
        return yaw;
    }

    public byte getPitch() {
        return pitch;
    }

    public short getCurrentItem() {
        return currentItem;
    }

    public List<DataWatcher.WatchableObject> getWatchableObjects() {
        return watchableObjects;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setCurrentItem(short currentItem) {
        this.currentItem = currentItem;
    }

}
