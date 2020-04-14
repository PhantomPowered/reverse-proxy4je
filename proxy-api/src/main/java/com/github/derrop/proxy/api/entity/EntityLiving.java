package com.github.derrop.proxy.api.entity;

import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public interface EntityLiving {

    @NotNull
    Location getLocation();

    void setLocation(@NotNull Location location);

    boolean isOnGround();

    int getEntityId();

    int getDimension();

    void setDimension(int dimension);

    boolean isDimensionChange();

    void setDimensionChange(boolean dimensionChange);

    @NotNull
    Unsafe unsafe();

    interface Unsafe {

        void setLocationUnchecked(@NotNull Location locationUnchecked);

    }

}
