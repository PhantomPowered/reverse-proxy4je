package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerLogoutEvent extends PlayerEvent { // TODO
    public PlayerLogoutEvent(@NotNull ProxiedPlayer player) {
        super(player);
    }
}
