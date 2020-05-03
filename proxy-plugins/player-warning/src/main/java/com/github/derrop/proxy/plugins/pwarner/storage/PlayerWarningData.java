package com.github.derrop.proxy.plugins.pwarner.storage;

import com.github.derrop.proxy.api.block.Material;

import java.util.*;

public class PlayerWarningData {

    private UUID targetPlayerId;
    private Map<Integer, Collection<WarnedEquipmentSlot>> equipmentSlots;

    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(UUID targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public boolean shouldWarnEquipmentSlot(int slotId, Material material) {
        return this.equipmentSlots.containsKey(slotId) && this.equipmentSlots.get(slotId).stream().anyMatch(slot -> slot.getMaterial() == material);
    }

    private PlayerWarningData(UUID targetPlayerId, Map<Integer, Collection<WarnedEquipmentSlot>> equipmentSlots) {
        this.targetPlayerId = targetPlayerId;
        this.equipmentSlots = equipmentSlots;
    }

    public boolean addEquipmentSlot(WarnedEquipmentSlot slot) {
        Collection<WarnedEquipmentSlot> slots = this.equipmentSlots.computeIfAbsent(slot.getSlot().getSlotId(), integer -> new ArrayList<>());
        if (slots.removeIf(slot1 -> slot1.getMaterial() == slot.getMaterial())) {
            return false;
        }
        slots.add(slot);
        return true;
    }

    public static PlayerWarningData newData(UUID uniqueId) {
        return new PlayerWarningData(uniqueId, new HashMap<>());
    }

    @Override
    public String toString() {
        return "PlayerWarningData{" +
                "targetPlayerId=" + targetPlayerId +
                ", equipmentSlots=" + equipmentSlots +
                '}';
    }
}
