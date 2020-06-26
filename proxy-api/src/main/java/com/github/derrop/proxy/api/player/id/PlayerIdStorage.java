package com.github.derrop.proxy.api.player.id;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerIdStorage {

    PlayerId getPlayerId(@NotNull String name);

    PlayerId getPlayerId(@NotNull UUID uniqueId);

    boolean isCached(@NotNull UUID uniqueId);

    boolean isCached(@NotNull String name);

}
