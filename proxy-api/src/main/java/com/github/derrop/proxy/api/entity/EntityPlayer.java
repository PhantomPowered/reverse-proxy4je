package com.github.derrop.proxy.api.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EntityPlayer extends EntityLiving {

    @NotNull
    UUID getUniqueId();

    @Nullable
    PlayerInfo getPlayerInfo();

    @NotNull
    PlayerSkinConfiguration getSkinConfiguration();

}
