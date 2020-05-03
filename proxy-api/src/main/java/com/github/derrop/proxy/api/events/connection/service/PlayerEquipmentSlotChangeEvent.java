package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;
import com.github.derrop.proxy.api.entity.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.util.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerEquipmentSlotChangeEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancel;

    private PlayerInfo playerInfo;
    private EquipmentSlot slot;
    private ItemStack item;

    public PlayerEquipmentSlotChangeEvent(@NotNull ServiceConnection connection, PlayerInfo playerInfo, EquipmentSlot slot, ItemStack item) {
        super(connection);
        this.playerInfo = playerInfo;
        this.slot = slot;
        this.item = item;
    }

    public PlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

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
