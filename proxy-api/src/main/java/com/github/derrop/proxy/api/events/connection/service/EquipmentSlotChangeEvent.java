package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.util.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentSlotChangeEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancel;

    private int entityId;
    private EquipmentSlot slot;
    private ItemStack item;

    public EquipmentSlotChangeEvent(@NotNull ServiceConnection connection, int entityId, EquipmentSlot slot, ItemStack item) {
        super(connection);
        this.entityId = entityId;
        this.slot = slot;
        this.item = item;
    }

    public int getEntityId() {
        return this.entityId;
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
