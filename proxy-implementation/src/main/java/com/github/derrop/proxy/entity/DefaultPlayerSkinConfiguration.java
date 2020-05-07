package com.github.derrop.proxy.entity;

import com.github.derrop.proxy.api.entity.PlayerSkinConfiguration;
import com.github.derrop.proxy.util.serialize.MinecraftSerializableObjectList;

import java.util.Arrays;

public abstract class DefaultPlayerSkinConfiguration implements PlayerSkinConfiguration {

    protected abstract MinecraftSerializableObjectList getObjectList();

    private byte getFlags() {
        return this.getObjectList().getByte(10);
    }

    @Override
    public SkinPart[] getEnabledParts() {
        byte flags = this.getFlags();
        return Arrays.stream(SkinPart.values())
                .filter(part -> this.has(flags, part))
                .toArray(SkinPart[]::new);
    }

    private boolean has(byte flags, SkinPart part) {
        return (flags & part.getMask()) == part.getMask();
    }

    @Override
    public boolean has(SkinPart part) {
        return this.has(this.getFlags(), part);
    }

    @Override
    public boolean hasCape() {
        return this.has(SkinPart.CAPE);
    }

    @Override
    public boolean hasJacket() {
        return this.has(SkinPart.JACKET);
    }

    @Override
    public boolean hasLeftSleeve() {
        return this.has(SkinPart.LEFT_SLEEVE);
    }

    @Override
    public boolean hasRightSleeve() {
        return this.has(SkinPart.RIGHT_SLEEVE);
    }

    @Override
    public boolean hasLeftPants() {
        return this.has(SkinPart.LEFT_PANTS);
    }

    @Override
    public boolean hasRightPants() {
        return this.has(SkinPart.RIGHT_PANTS);
    }

    @Override
    public boolean hasHat() {
        return this.has(SkinPart.HAT);
    }
}
