package com.github.derrop.proxy.plugins.pwarner.storage;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;

public class WarnedEquipmentSlot {

    private EquipmentSlot slot;
    private Material material;
    private ChatColor color;

    public WarnedEquipmentSlot(EquipmentSlot slot, Material material, ChatColor color) {
        this.slot = slot;
        this.material = material;
        this.color = color;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public void setSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "WarnedEquipmentSlot{" +
                "slot=" + slot +
                ", material=" + material +
                '}';
    }
}
