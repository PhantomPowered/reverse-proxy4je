package com.github.derrop.proxy.api.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockState {

    int getId();

    @NotNull
    Material getMaterial();

    boolean isPowered();

    boolean isOpen();

    int getLayers();

    double getHeight();

    double getThickness();

    boolean isPassable();

    int getRedstonePower();

    @Nullable
    SubMaterial getSubMaterial();

    @Nullable
    HingePosition getHingePosition();

    @Nullable
    Facing getFacing();

    @Nullable
    TrapdoorPosition getHalf();

}
