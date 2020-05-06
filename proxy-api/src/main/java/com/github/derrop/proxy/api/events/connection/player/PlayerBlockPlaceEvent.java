package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.util.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockPlaceEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;

    private final BlockPos pos;
    private final ItemStack item;

    public PlayerBlockPlaceEvent(@NotNull Player player, @NotNull BlockPos pos, @NotNull ItemStack item) {
        super(player);
        this.pos = pos;
        this.item = item;
    }

    @NotNull
    public BlockPos getPos() {
        return this.pos;
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
