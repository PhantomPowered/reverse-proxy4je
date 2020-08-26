package com.github.phantompowered.proxy.api.player.id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerIdStorage {

    PlayerId getPlayerId(@NotNull String name);

    PlayerId getPlayerId(@NotNull UUID uniqueId);

    PlayerId getPlayerId(@Nullable String name, @Nullable UUID uniqueId);

    boolean isCached(@NotNull UUID uniqueId);

    boolean isCached(@NotNull String name);
}
