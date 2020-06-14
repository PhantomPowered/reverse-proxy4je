package com.github.derrop.proxy.api.util.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerIdRepository {

    PlayerId getPlayerId(@NotNull String name);

    PlayerId getPlayerId(@NotNull UUID uniqueId);

    boolean isCached(@NotNull UUID uniqueId);

    boolean isCached(@NotNull String name);

}
