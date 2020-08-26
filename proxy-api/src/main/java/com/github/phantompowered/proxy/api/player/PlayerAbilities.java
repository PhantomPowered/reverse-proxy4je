package com.github.phantompowered.proxy.api.player;

public interface PlayerAbilities {

    boolean isInvulnerable();

    boolean isFlying();

    void setFlying(boolean flying);

    boolean isAllowedFlying();

    boolean isCreativeMode();

    float getFlightSpeed();

    float getWalkSpeed();

}
