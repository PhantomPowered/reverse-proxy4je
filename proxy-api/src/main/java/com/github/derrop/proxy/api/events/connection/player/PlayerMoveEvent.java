package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public class PlayerMoveEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;

    private final Location from;
    private Location to;

    public PlayerMoveEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to) {

        super(player);
        this.from = from;
        this.to = to;
    }

    @NotNull
    public Location getFrom() {
        return this.from;
    }

    @NotNull
    public Location getTo() {
        return this.to;
    }

    public void setTo(@NotNull Location to) {
        this.to = to;
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
