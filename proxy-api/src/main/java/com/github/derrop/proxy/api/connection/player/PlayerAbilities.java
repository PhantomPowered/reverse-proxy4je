package com.github.derrop.proxy.api.connection.player;

public interface PlayerAbilities {

    boolean isInvulnerable();

    boolean isFlying();

    boolean isAllowedFlying();

    boolean isCreativeMode();

    float getFlightSpeed();

    float getWalkSpeed();

    void setFlying(boolean flying);

}
