package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerLogoutEvent extends PlayerEvent {
    public PlayerLogoutEvent(@NotNull Player player) {
        super(player);
    }
}
