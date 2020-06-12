package com.github.derrop.proxy.plugins.pwarner.storage;

import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;

public class WarnedEquipmentSlot {

    private EquipmentSlot slot;
    private Material material;
    // TODO add color

    public WarnedEquipmentSlot(EquipmentSlot slot, Material material) {
        this.slot = slot;
        this.material = material;
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

    @Override
    public String toString() {
        return "WarnedEquipmentSlot{" +
                "slot=" + slot +
                ", material=" + material +
                '}';
    }
}
