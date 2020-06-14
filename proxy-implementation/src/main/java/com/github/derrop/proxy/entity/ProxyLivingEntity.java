package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.entity.EntityLiving;
import com.github.derrop.proxy.api.entity.LivingEntityType;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;

public class ProxyLivingEntity extends ProxyEntity implements EntityLiving {

    protected ProxyLivingEntity(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, LivingEntityType type) {
        super(registry, client, spawnPacket, type.getTypeId());
    }

    @Override
    public float getHealth() {
        return this.objectList.getFloat(6);
    }

    @Override
    public boolean hasPotionEffectColor() {
        return this.objectList.getInt(7) > 0;
    }

    @Override
    public int getPotionEffectColor() {
        return this.objectList.getInt(7);
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return this.objectList.getByte(8) != 0;
    }

    @Override
    public byte getArrowsInBody() {
        return this.objectList.getByte(9);
    }

    @Override
    public boolean hasAi() {
        return this.objectList.getByte(15) != 0;
    }
}
