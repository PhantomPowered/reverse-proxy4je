package com.github.phantompowered.proxy.api.events.connection.player;

import com.github.phantompowered.proxy.api.event.Cancelable;
import com.github.phantompowered.proxy.api.player.Player;
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
