package com.github.phantompowered.proxy.api.events.connection.player;

import com.github.phantompowered.proxy.api.event.Cancelable;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryClickEvent extends PlayerEvent implements Cancelable {

    private final int clickedSlot;
    private final ClickType click;
    private boolean cancel;

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
