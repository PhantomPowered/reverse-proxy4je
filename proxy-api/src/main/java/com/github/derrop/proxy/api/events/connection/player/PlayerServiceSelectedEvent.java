package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerServiceSelectedEvent extends PlayerEvent {

    private final ServiceConnection connection;

    public PlayerServiceSelectedEvent(@NotNull Player player, @NotNull ServiceConnection connection) {
        super(player);
        this.connection = connection;
    }

    @NotNull
    @Override
    public ServiceConnection getConnection() {
        return this.connection;
    }
}
