package com.github.derrop.proxy.api.player;

public interface PlayerAbilities {

    boolean isInvulnerable();

    boolean isFlying();

    boolean isAllowedFlying();

    boolean isCreativeMode();

    float getFlightSpeed();

    float getWalkSpeed();

}
