package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerKickEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;
    private BaseComponent[] reason;

    public PlayerKickEvent(@NotNull Player player, @Nullable BaseComponent[] reason) {
        super(player);
        this.reason = reason;
    }

    @Nullable
    public BaseComponent[] getReason() {
        return this.reason;
    }

    public void setReason(@Nullable BaseComponent[] reason) {
        this.reason = reason;
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
