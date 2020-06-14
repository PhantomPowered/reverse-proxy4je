package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockPlaceEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;

    private final Location location;
    private final ItemStack item;

    public PlayerBlockPlaceEvent(@NotNull Player player, @NotNull Location location, @NotNull ItemStack item) {
        super(player);
        this.location = location;
        this.item = item;
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    @NotNull
    public ItemStack getItem() {
        return this.item;
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
