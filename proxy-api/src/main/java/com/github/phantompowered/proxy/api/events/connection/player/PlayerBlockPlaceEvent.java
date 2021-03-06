package com.github.phantompowered.proxy.api.events.connection.player;

import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.event.Cancelable;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.api.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockPlaceEvent extends PlayerEvent implements Cancelable {

    private final Location location;
    private final Location blockLocation;
    private final ItemStack item;
    private final Facing facing;
    private final Vector placeVector;
    private boolean cancel;

    public PlayerBlockPlaceEvent(@NotNull Player player, @NotNull Location location, Location blockLocation, @NotNull ItemStack item, @NotNull Facing facing, @NotNull Vector placeVector) {
        super(player);
        this.location = location;
        this.blockLocation = blockLocation;
        this.item = item;
        this.facing = facing;
        this.placeVector = placeVector;
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    @NotNull
    public Location getBlockLocation() {
        return this.blockLocation;
    }

    @NotNull
    public ItemStack getItem() {
        return this.item;
    }

    @NotNull
    public Facing getFacing() {
        return this.facing;
    }

    @NotNull
    public Vector getPlaceVector() {
        return this.placeVector;
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
