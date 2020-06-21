/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.item;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.item.*;
import com.github.derrop.proxy.api.util.nbt.NBTBase;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import com.github.derrop.proxy.api.util.nbt.NBTTagList;
import com.github.derrop.proxy.api.util.nbt.NBTTagString;
import com.google.common.base.CaseFormat;
import com.google.common.base.Enums;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.kyori.text.TranslatableComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProxyItemMeta implements ItemMeta {

    @NotNull
    public static ItemMeta createFromItemId(int itemId, int meta, NBTTagCompound data) {
        switch (itemId) {
            case 373:
                return new ProxyPotionMeta(data, meta);
            case 386:
                return new ProxyBookMeta(data, false);
            case 387:
                return new ProxyBookMeta(data, true);
            case 397:
                return new ProxySkullMeta(data);
            case 403:
                return new ProxyEnchantedBookMeta(data);
            default:
                return new ProxyItemMeta(data);
        }
    }

    // ===

    private final Map<String, NBTBase> unhandled = Maps.newHashMap();

    private Map<Enchantment, Integer> enchantments = Maps.newHashMap();
    private Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
    private List<TranslatableComponent> lore = Lists.newArrayList();

    private TranslatableComponent displayName;
    private TranslatableComponent locName;
    private boolean unbreakable;
    private int hideFlag;
    private int repairCost;
    private int damage;
    private int customModelData;

    private NBTTagCompound blockData;

    public ProxyItemMeta(@NotNull NBTTagCompound source) {
        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.DISPLAY)) {
            NBTTagCompound display = source.getCompoundTag(ProxyItemMeta.ItemMetaKeys.DISPLAY);
            if (display.hasKey(ProxyItemMeta.ItemMetaKeys.NAME)) {
                try {
                    this.displayName = TranslatableComponent.of(limit(display.getString(ProxyItemMeta.ItemMetaKeys.NAME), 1024)); // vanilla limit
                } catch (Throwable ignored) {
                }
            }

            if (display.hasKey(ProxyItemMeta.ItemMetaKeys.LOC_NAME)) {
                try {
                    this.locName = TranslatableComponent.of(limit(display.getString(ProxyItemMeta.ItemMetaKeys.LOC_NAME), 1024)); // vanilla limit
                } catch (Throwable ignored) {
                }
            }

            if (display.hasKey(ProxyItemMeta.ItemMetaKeys.LORE)) {
                NBTTagList lore = display.getTagList(ProxyItemMeta.ItemMetaKeys.LORE, ProxyItemMeta.NbtTagNumbers.TAG_STRING);
                for (int index = 0; index < lore.tagCount(); index++) {
                    String line = limit(lore.getStringTagAt(index), 8192); // vanilla limit
                    try {
                        this.lore.add(TranslatableComponent.of(line));
                    } catch (Throwable ignored) {
                    }
                }
            }
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.CUSTOM_MODEL_DATA, ProxyItemMeta.NbtTagNumbers.TAG_INT)) {
            this.customModelData = source.getInteger(ProxyItemMeta.ItemMetaKeys.CUSTOM_MODEL_DATA);
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.BLOCK_DATA, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND)) {
            this.blockData = source.getCompoundTag(ProxyItemMeta.ItemMetaKeys.BLOCK_DATA);
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.ENCHANTMENTS)) {
            this.enchantments.putAll(this.buildEnchantments(source, ProxyItemMeta.ItemMetaKeys.ENCHANTMENTS));
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES, ProxyItemMeta.NbtTagNumbers.TAG_LIST)) {
            NBTTagList mods = source.getTagList(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND);
            for (int i = 0; i < mods.tagCount(); i++) {
                NBTTagCompound compound = mods.getCompoundTagAt(i);
                if (compound.hasNoTags()) {
                    continue;
                }

                AttributeModifier modifier = this.create(compound);
                if (modifier == null) {
                    continue;
                }

                String attributeName = compound.getString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_IDENTIFIER);
                if (attributeName == null || attributeName.isEmpty()) {
                    continue;
                }

                Attribute attribute = this.findAttribute(attributeName);
                if (attribute == null) {
                    continue;
                }

                if (compound.hasKey(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_SLOT, ProxyItemMeta.NbtTagNumbers.TAG_STRING)) {
                    String slotName = compound.getString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_SLOT);
                    if (slotName == null || slotName.isEmpty()) {
                        this.modifiers.put(attribute, modifier);
                        continue;
                    }

                    EquipmentSlot slot = EquipmentSlot.getByName(slotName);
                    if (slot == null) {
                        this.modifiers.put(attribute, modifier);
                        continue;
                    }

                    modifier = new AttributeModifier(
                            modifier.getUniqueId(),
                            modifier.getName(),
                            modifier.getAmount(),
                            modifier.getOperation(),
                            slot
                    );
                }

                this.modifiers.put(attribute, modifier);
            }
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.REPAIR)) {
            this.repairCost = source.getInteger(ProxyItemMeta.ItemMetaKeys.REPAIR);
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.HIDE_FLAGS)) {
            this.hideFlag = source.getInteger(ProxyItemMeta.ItemMetaKeys.HIDE_FLAGS);
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.UNBREAKABLE)) {
            this.unbreakable = source.getBoolean(ProxyItemMeta.ItemMetaKeys.UNBREAKABLE);
        }

        if (source.hasKey(ProxyItemMeta.ItemMetaKeys.DAMAGE)) {
            this.damage = source.getInteger(ProxyItemMeta.ItemMetaKeys.DAMAGE);
        }

        for (Map.Entry<String, NBTBase> stringNBTBaseEntry : source.getEntrySet()) {
            if (!HANDLED.contains(stringNBTBaseEntry.getKey())) {
                this.unhandled.put(stringNBTBaseEntry.getKey(), stringNBTBaseEntry.getValue());
            }
        }
    }

    private Attribute findAttribute(@NotNull String name) {
        String[] split = name.split("\\.", 2);
        if (split.length != 2) {
            return null;
        }

        String expectedName = split[0] + "_" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, split[1]);
        return Enums.getIfPresent(Attribute.class, expectedName.toUpperCase(Locale.ROOT)).orNull();
    }

    private AttributeModifier create(@NotNull NBTTagCompound compound) {
        UUID uuid = new UUID(compound.getLong(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_UUID_HIGH), compound.getLong(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_UUID_LOW));

        try {
            AttributeModifier.Operation operation = AttributeModifier.Operation.values()[compound.getInteger(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_TYPE)];
            return new AttributeModifier(
                    uuid,
                    compound.getString(compound.getString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_NAME)),
                    compound.getDouble(compound.getString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_VALUE)),
                    operation
            );
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    @NotNull
    protected Map<Enchantment, Integer> buildEnchantments(@NotNull NBTTagCompound source, @NotNull String key) {
        Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<>();

        NBTTagList enchants = source.getTagList(key, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND);
        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound compound = enchants.getCompoundTagAt(i);
            short id = compound.getShort(ProxyItemMeta.ItemMetaKeys.ENCHANTMENT_ID);
            int level = 0xffff & compound.getShort(ProxyItemMeta.ItemMetaKeys.ENCHANTMENT_LEVEL);

            Enchantment enchantment = Enchantment.getById(id);
            if (enchantment != null) {
                enchantmentIntegerMap.put(enchantment, level);
            }
        }

        return enchantmentIntegerMap;
    }

    protected void writeEnchantments(@NotNull Map<Enchantment, Integer> enchantments, @NotNull NBTTagCompound target, @NotNull String key) {
        target.setTag(key, new NBTTagList());
        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchantments.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setShort(ProxyItemMeta.ItemMetaKeys.ENCHANTMENT_ID, enchantmentIntegerEntry.getKey().getId());
            compound.setShort(ProxyItemMeta.ItemMetaKeys.ENCHANTMENT_LEVEL, enchantmentIntegerEntry.getValue().shortValue());

            target.getTagList(key, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND).appendTag(compound);
        }
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        Set<ItemFlag> currentFlags = EnumSet.noneOf(ItemFlag.class);
        for (ItemFlag f : ItemFlag.values()) {
            if (hasItemFlag(f)) {
                currentFlags.add(f);
            }
        }

        return currentFlags;
    }

    @Override
    public void addItemFlags(@NonNls ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.hideFlag |= this.getBitModifier(itemFlag);
        }
    }

    @Override
    public void removeItemFlags(@NonNls ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.hideFlag &= ~this.getBitModifier(itemFlag);
        }
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        int bitModifier = this.getBitModifier(flag);
        return (this.hideFlag & bitModifier) == bitModifier;
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    @Override
    public void setEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public void setModifiers(@NotNull Multimap<Attribute, AttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public @NotNull List<TranslatableComponent> getLore() {
        return lore;
    }

    @Override
    public void setLore(@NotNull List<TranslatableComponent> lore) {
        this.lore = lore;
    }

    @Override
    public @NotNull TranslatableComponent getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(@NotNull TranslatableComponent displayName) {
        this.displayName = displayName;
    }

    @Override
    public @NotNull TranslatableComponent getLocName() {
        return locName;
    }

    @Override
    public void setLocName(@NotNull TranslatableComponent locName) {
        this.locName = locName;
    }

    @Override
    public int getRepairCost() {
        return repairCost;
    }

    @Override
    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    @Override
    public boolean isUnbreakable() {
        return unbreakable;
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public int getCustomModelData() {
        return customModelData;
    }

    @Override
    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    @Override
    public @NotNull NBTTagCompound getBlockData() {
        return blockData;
    }

    @Override
    public void setBlockData(@NotNull NBTTagCompound blockData) {
        this.blockData = blockData;
    }

    private byte getBitModifier(ItemFlag hideFlag) {
        return (byte) (1 << hideFlag.ordinal());
    }

    @NotNull
    protected static String limit(@NotNull String s, int limit) {
        if (s.length() > limit) {
            s = s.substring(0, limit);
        }

        return s;
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (this.displayName != null || this.locName != null || !this.lore.isEmpty()) {
            nbtTagCompound.setTag(ProxyItemMeta.ItemMetaKeys.DISPLAY, new NBTTagCompound());
            if (this.displayName != null) {
                nbtTagCompound.getCompoundTag(ProxyItemMeta.ItemMetaKeys.DISPLAY).setString(ProxyItemMeta.ItemMetaKeys.NAME, this.displayName.key());
            }

            if (this.locName != null) {
                nbtTagCompound.getCompoundTag(ProxyItemMeta.ItemMetaKeys.DISPLAY).setString(ProxyItemMeta.ItemMetaKeys.LOC_NAME, this.locName.key());
            }

            if (!this.lore.isEmpty()) {
                nbtTagCompound.getCompoundTag(ProxyItemMeta.ItemMetaKeys.DISPLAY).setTag(ProxyItemMeta.ItemMetaKeys.LORE, new NBTTagList());
                for (TranslatableComponent translatableComponent : this.lore) {
                    nbtTagCompound.getCompoundTag(ProxyItemMeta.ItemMetaKeys.DISPLAY)
                            .getTagList(ProxyItemMeta.ItemMetaKeys.LORE, ProxyItemMeta.NbtTagNumbers.TAG_STRING)
                            .appendTag(new NBTTagString(translatableComponent.key()));
                }
            }
        }

        if (this.customModelData != 0) {
            nbtTagCompound.setInteger(ProxyItemMeta.ItemMetaKeys.CUSTOM_MODEL_DATA, this.customModelData);
        }

        if (this.blockData != null) {
            nbtTagCompound.setTag(ProxyItemMeta.ItemMetaKeys.BLOCK_DATA, this.blockData);
        }

        nbtTagCompound.setBoolean(ProxyItemMeta.ItemMetaKeys.UNBREAKABLE, this.unbreakable);

        if (this.repairCost > 0) {
            nbtTagCompound.setInteger(ProxyItemMeta.ItemMetaKeys.REPAIR, this.repairCost);
        }

        if (this.hideFlag != 0) {
            nbtTagCompound.setInteger(ProxyItemMeta.ItemMetaKeys.HIDE_FLAGS, this.hideFlag);
        }

        if (this.damage > 0) {
            nbtTagCompound.setInteger(ProxyItemMeta.ItemMetaKeys.DAMAGE, this.damage);
        }

        if (!this.enchantments.isEmpty()) {
            this.writeEnchantments(this.enchantments, nbtTagCompound, ProxyItemMeta.ItemMetaKeys.ENCHANTMENTS);
        }

        if (!this.modifiers.isEmpty()) {
            nbtTagCompound.setTag(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES, new NBTTagList());
            for (Map.Entry<Attribute, AttributeModifier> entry : this.modifiers.entries()) {
                NBTTagCompound compound = new NBTTagCompound();

                compound.setLong(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_UUID_HIGH, entry.getValue().getUniqueId().getMostSignificantBits());
                compound.setLong(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_UUID_LOW, entry.getValue().getUniqueId().getLeastSignificantBits());
                compound.setInteger(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_TYPE, entry.getValue().getOperation().ordinal());
                compound.setString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_NAME, entry.getValue().getName());
                compound.setDouble(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_VALUE, entry.getValue().getAmount());

                if (entry.getValue().getSlot() != null) {
                    compound.setString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_SLOT, entry.getValue().getSlot().getSlotNameNms());
                }

                compound.setString(ProxyItemMeta.ItemMetaKeys.ATTRIBUTES_IDENTIFIER, entry.getKey().name().replaceFirst("_", ".").toLowerCase());
            }
        }

        for (Map.Entry<String, NBTBase> stringNBTBaseEntry : this.unhandled.entrySet()) {
            nbtTagCompound.setTag(stringNBTBaseEntry.getKey(), stringNBTBaseEntry.getValue());
        }

        return nbtTagCompound;
    }

    protected static final Collection<String> HANDLED = new ArrayList<>(Arrays.asList(
            ProxyItemMeta.ItemMetaKeys.DISPLAY,
            ProxyItemMeta.ItemMetaKeys.BLOCK_DATA,
            ProxyItemMeta.ItemMetaKeys.CUSTOM_MODEL_DATA,
            ProxyItemMeta.ItemMetaKeys.ENCHANTMENTS,
            ProxyItemMeta.ItemMetaKeys.ATTRIBUTES,
            ProxyItemMeta.ItemMetaKeys.REPAIR,
            ProxyItemMeta.ItemMetaKeys.HIDE_FLAGS,
            ProxyItemMeta.ItemMetaKeys.UNBREAKABLE,
            ProxyItemMeta.ItemMetaKeys.DAMAGE
    ));

    public interface ItemMetaKeys {

        String NAME = "Name";
        String LOC_NAME = "LocName";
        String DISPLAY = "display";
        String LORE = "Lore";
        String CUSTOM_MODEL_DATA = "CustomModelData";
        String ENCHANTMENTS = "ench";
        String ENCHANTMENT_ID = "id";
        String ENCHANTMENT_LEVEL = "lvl";
        String REPAIR = "RepairCost";
        String ATTRIBUTES = "AttributeModifiers";
        String ATTRIBUTES_IDENTIFIER = "AttributeName";
        String ATTRIBUTES_NAME = "Name";
        String ATTRIBUTES_VALUE = "Amount";
        String ATTRIBUTES_TYPE = "Operation";
        String ATTRIBUTES_UUID_HIGH = "Most";
        String ATTRIBUTES_UUID_LOW = "Least";
        String ATTRIBUTES_SLOT = "Slot";
        String HIDE_FLAGS = "HideFlags";
        String UNBREAKABLE = "Unbreakable";
        String DAMAGE = "Damage";
        String BLOCK_DATA = "BlockStateTag";
    }

    public interface NbtTagNumbers {

        int TAG_END = 0;
        int TAG_BYTE = 1;
        int TAG_SHORT = 2;
        int TAG_INT = 3;
        int TAG_LONG = 4;
        int TAG_FLOAT = 5;
        int TAG_DOUBLE = 6;
        int TAG_BYTE_ARRAY = 7;
        int TAG_STRING = 8;
        int TAG_LIST = 9;
        int TAG_COMPOUND = 10;
        int TAG_INT_ARRAY = 11;
        int TAG_ANY_NUMBER = 99;
    }
}
