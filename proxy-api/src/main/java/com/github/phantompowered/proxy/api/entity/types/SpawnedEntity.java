package com.github.phantompowered.proxy.api.entity.types;

import com.github.phantompowered.proxy.api.entity.EntityEffect;

public interface SpawnedEntity extends Entity {

    EntityEffect createEffect(byte effectId, byte amplifier, int duration, boolean hideParticles);

    void addEffect(EntityEffect effect);

    void removeEffect(byte effectId);

    EntityEffect[] getActiveEffects();

}
