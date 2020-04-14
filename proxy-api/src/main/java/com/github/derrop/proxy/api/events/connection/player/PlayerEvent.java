package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.events.connection.ConnectionEvent;
import com.github.derrop.proxy.api.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEvent extends ConnectionEvent {

    public PlayerEvent(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    private final Player player;

    @NotNull
    public Player getPlayer() {
        return player;
    }

}
