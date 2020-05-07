package com.github.derrop.proxy.api.entity;

import org.jetbrains.annotations.Nullable;

// TODO Move to EntityLiving, EntityArmorStand, EntityMob, ...
public interface EntityWithMetadata extends Entity {

    boolean isSilent();

    void setSilent(boolean silent);

    boolean isCustomNameVisible();

    void setCustomNameVisible(boolean customNameVisible);

    boolean hasCustomName();

    @Nullable
    String getCustomName();

    void setCustomName(String name);

    boolean isNoAI();

    void setNoAI(boolean noAI);

    /**
     * Returns true if the flag is active for this entity.
     * Flags:
     * - 0 = is burning
     * - 1 = is sneaking
     * - 2 = is riding
     * - 3 = is sprinting
     * - 4 = is eating
     * - 5 = is invisible
     */
    boolean getFlag(int flag);

    boolean isBurning();

    boolean isSneaking();

    boolean isRiding();

    boolean isSprinting();

    boolean isEating();

    boolean isInvisible();

    /**
     * Under water air
     */
    short getAir();

    void setAir(short air);

}
