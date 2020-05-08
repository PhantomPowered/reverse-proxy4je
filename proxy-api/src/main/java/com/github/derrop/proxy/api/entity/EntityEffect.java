package com.github.derrop.proxy.api.entity;

public interface EntityEffect {

    byte getEffectId();

    byte getAmplifier();

    long getTimeout();

    boolean isInfinite();

    boolean isHidingParticles();

    int getInitialDurationTicks();

    boolean isValid();

}
