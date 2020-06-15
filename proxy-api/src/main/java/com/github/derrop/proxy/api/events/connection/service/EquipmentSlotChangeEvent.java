package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentSlotChangeEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancel;

    private Entity entity;
    private EquipmentSlot slot;
    private ItemStack item;

    public EquipmentSlotChangeEvent(@NotNull ServiceConnection connection, Entity entity, EquipmentSlot slot, ItemStack item) {
        super(connection);
        this.entity = entity;
        this.slot = slot;
        this.item = item;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
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
