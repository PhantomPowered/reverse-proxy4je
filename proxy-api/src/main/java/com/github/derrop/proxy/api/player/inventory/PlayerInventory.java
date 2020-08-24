package com.github.derrop.proxy.api.player.inventory;

import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface PlayerInventory {

    void open();

    void close();

    Player getPlayer();

    Component getTitle();

    byte getWindowId();

    void setWindowId(byte windowId);

    void setTitle(Component component);

    InventoryType getType();

    void setType(InventoryType type);

    void setContent(@NotNull ItemStack[] items);

    @NotNull
    ItemStack[] getContent();

    void setItem(int slot, ItemStack item);

    @NotNull
    ItemStack getItem(int slot);

}
