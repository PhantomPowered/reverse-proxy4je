package com.github.derrop.proxy.api.entity;

public interface SpawnedEntity extends Entity {

    EntityEffect createEffect(byte effectId, byte amplifier, int duration, boolean hideParticles);

    void addEffect(EntityEffect effect);

    void removeEffect(byte effectId);

    EntityEffect[] getActiveEffects();

}
