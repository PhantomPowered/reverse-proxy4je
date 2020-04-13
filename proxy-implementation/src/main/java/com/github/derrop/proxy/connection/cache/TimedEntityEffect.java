package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.connection.cache.packet.entity.effect.EntityEffect;

public class TimedEntityEffect {

    private int entityId;
    private byte effectId;
    private byte amplifier;
    private long timeout;
    private byte hideParticles;

    private TimedEntityEffect(int entityId, byte effectId, byte amplifier, long timeout, byte hideParticles) {
        this.entityId = entityId;
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.timeout = timeout;
        this.hideParticles = hideParticles;
    }

    public int getEntityId() {
        return entityId;
    }

    public byte getEffectId() {
        return effectId;
    }

    public byte getAmplifier() {
        return amplifier;
    }

    public long getTimeout() {
        return timeout;
    }

    public byte getHideParticles() {
        return hideParticles;
    }

    public static TimedEntityEffect fromEntityEffect(EntityEffect entityEffect) {
        long durationMillis = entityEffect.getDuration() >= 32767 ? -1 : ((entityEffect.getDuration() / 20) * 1000) + System.currentTimeMillis();

        return new TimedEntityEffect(
                entityEffect.getEntityId(), entityEffect.getEffectId(),
                entityEffect.getAmplifier(), durationMillis,
                entityEffect.getHideParticles()
        );
    }

    public EntityEffect toEntityEffect() {
        if (System.currentTimeMillis() >= this.timeout) {
            return null;
        }

        int durationTicks = this.timeout == -1 ? 32767 : (int) (((this.timeout - System.currentTimeMillis()) / 1000) * 20);
        return new EntityEffect(this.entityId, this.effectId, this.amplifier, durationTicks, this.hideParticles);
    }

}
