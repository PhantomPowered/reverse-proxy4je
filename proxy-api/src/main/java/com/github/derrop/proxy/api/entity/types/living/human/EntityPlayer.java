package com.github.derrop.proxy.api.entity.types.living.human;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EntityPlayer extends EntityHuman {

    @NotNull
    UUID getUniqueId();

    @Nullable
    PlayerInfo getPlayerInfo();
}
