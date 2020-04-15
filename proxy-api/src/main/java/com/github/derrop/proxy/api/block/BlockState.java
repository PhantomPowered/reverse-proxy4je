package com.github.derrop.proxy.api.block;

import com.github.derrop.proxy.api.util.EnumFacing;
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

    @Nullable
    SubMaterial getSubMaterial();

    @Nullable
    HingePosition getHingePosition();

    @Nullable
    EnumFacing getFacing();

    @Nullable
    TrapdoorPosition getHalf();

}
