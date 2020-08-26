package com.github.phantompowered.proxy.api.player.inventory;

import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.player.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface PlayerInventory {

    void open();

    void close();

    Player getPlayer();

    Component getTitle();

    void setTitle(Component component);

    byte getWindowId();

    void setWindowId(byte windowId);

    InventoryType getType();

    void setType(InventoryType type);

    @NotNull
    ItemStack[] getContent();

    void setContent(@NotNull ItemStack[] items);

    void setItem(int slot, ItemStack item);

    @NotNull
    ItemStack getItem(int slot);

}
