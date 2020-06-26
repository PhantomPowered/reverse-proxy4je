package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryCloseEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;

    public PlayerInventoryCloseEvent(@NotNull Player player) {
        super(player);
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
}
