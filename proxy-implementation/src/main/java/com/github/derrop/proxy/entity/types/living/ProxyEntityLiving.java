package com.github.derrop.proxy.entity.types.living;

import com.github.derrop.proxy.api.entity.types.living.EntityLiving;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.entity.ProxyEntity;

public class ProxyEntityLiving extends ProxyEntity implements EntityLiving {

    public ProxyEntityLiving(ServiceRegistry registry, ConnectedProxyClient client, PositionedPacket spawnPacket, Object type) {
        super(registry, client, spawnPacket, type);
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
}
