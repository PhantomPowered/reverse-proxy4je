package com.github.phantompowered.proxy.api.entity.types.living.human;

import com.github.phantompowered.proxy.api.block.Material;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.player.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface EntityPlayer extends EntityHuman {

    @NotNull
    UUID getUniqueId();

    @Nullable
    PlayerInfo getPlayerInfo();

    boolean isBlocking();

    boolean isShootingWithBow();

    ItemStack getEquipmentSlot(@NotNull EquipmentSlot slot);

    Material getMaterialInEquipmentSlot(@NotNull EquipmentSlot slot);

}
