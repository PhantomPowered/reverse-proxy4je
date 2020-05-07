package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.connection.player.inventory.ClickType;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryClickEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;
    private int clickedSlot;
    private ClickType click;

    public PlayerInventoryClickEvent(@NotNull Player player, int clickedSlot, ClickType click) {
        super(player);
        this.clickedSlot = clickedSlot;
        this.click = click;
    }

    public int getClickedSlot() {
        return this.clickedSlot;
    }

    public ClickType getClick() {
        return this.click;
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
