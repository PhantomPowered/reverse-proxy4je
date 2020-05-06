package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public class PlayerBlockBreakEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;

    private final BlockPos pos;
    private final Action action;

    public PlayerBlockBreakEvent(@NotNull Player player, @NotNull BlockPos pos, @NotNull Action action) {
        super(player);
        this.pos = pos;
        this.action = action;
    }

    @NotNull
    public BlockPos getPos() {
        return this.pos;
    }

    @NotNull
    public Action getAction() {
        return this.action;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    public enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM;
    }

}
