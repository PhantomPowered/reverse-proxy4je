package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.entity.EntityType;
import com.github.derrop.proxy.api.entity.EntityWithMetadata;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;

public class CachedEntityWithMetadata extends CachedEntity implements EntityWithMetadata {

    protected CachedEntityWithMetadata(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, EntityType type) {
        super(registry, client, spawnPacket, type);
    }

    @Override
    public boolean isSilent() {
        return super.objectList.getBoolean(4);
    }

    @Override
    public void setSilent(boolean silent) {
        super.objectList.updateByte(4, silent ? 1 : 0);
    }

    @Override
    public boolean isCustomNameVisible() {
        return super.objectList.getBoolean(3);
    }

    @Override
    public void setCustomNameVisible(boolean customNameVisible) {
        super.objectList.updateBoolean(3, customNameVisible);
    }

    @Override
    public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    @Override
    public String getCustomName() {
        String customName = super.objectList.getString(2);
        return customName == null || customName.isEmpty() ? null : customName;
    }

    @Override
    public void setCustomName(String name) {
        super.objectList.updateString(2, name);
    }

    @Override
    public boolean isNoAI() {
        return super.objectList.getBoolean(15);
    }

    @Override
    public void setNoAI(boolean noAI) {
        super.objectList.updateBoolean(15, noAI);
    }

    @Override
    public boolean getFlag(int flag) {
        return (super.objectList.getByte(0) & 1 << flag) != 0;
    }

    @Override
    public boolean isBurning() {
        return this.getFlag(0);
    }

    @Override
    public boolean isSneaking() {
        return this.getFlag(1);
    }

    @Override
    public boolean isRiding() {
        return this.getFlag(2);
    }

    @Override
    public boolean isSprinting() {
        return this.getFlag(3);
    }

    @Override
    public boolean isEating() {
        return this.getFlag(4);
    }

    @Override
    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Override
    public short getAir() {
        return super.objectList.getShort(1);
    }

    @Override
    public void setAir(short air) {
        super.objectList.updateShort(1, air);
    }
}
