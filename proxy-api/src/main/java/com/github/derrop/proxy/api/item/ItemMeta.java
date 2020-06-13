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
package com.github.derrop.proxy.api.item;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
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

public class ItemMeta {

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

    public ItemMeta(@NotNull NBTTagCompound source) {
        if (source.hasKey(ItemMetaKeys.DISPLAY)) {
            NBTTagCompound display = source.getCompoundTag(ItemMetaKeys.DISPLAY);
            if (display.hasKey(ItemMetaKeys.NAME)) {
                try {
                    this.displayName = TranslatableComponent.of(limit(display.getString(ItemMetaKeys.NAME), 1024)); // vanilla limit
                } catch (Throwable ignored) {
                }
            }

            if (display.hasKey(ItemMetaKeys.LOC_NAME)) {
                try {
                    this.locName = TranslatableComponent.of(limit(display.getString(ItemMetaKeys.LOC_NAME), 1024)); // vanilla limit
                } catch (Throwable ignored) {
                }
            }

            if (display.hasKey(ItemMetaKeys.LORE)) {
                NBTTagList lore = display.getTagList(ItemMetaKeys.LORE, NbtTagNumbers.TAG_STRING);
                for (int index = 0; index < lore.tagCount(); index++) {
                    String line = limit(lore.getStringTagAt(index), 8192); // vanilla limit
                    try {
                        this.lore.add(TranslatableComponent.of(line));
                    } catch (Throwable ignored) {
                    }
                }
            }
        }

        if (source.hasKey(ItemMetaKeys.CUSTOM_MODEL_DATA, NbtTagNumbers.TAG_INT)) {
            this.customModelData = source.getInteger(ItemMetaKeys.CUSTOM_MODEL_DATA);
        }

        if (source.hasKey(ItemMetaKeys.BLOCK_DATA, NbtTagNumbers.TAG_COMPOUND)) {
            this.blockData = source.getCompoundTag(ItemMetaKeys.BLOCK_DATA);
        }

        if (source.hasKey(ItemMetaKeys.ENCHANTMENTS)) {
            NBTTagList enchants = source.getTagList(ItemMetaKeys.ENCHANTMENTS, NbtTagNumbers.TAG_COMPOUND);
            for (int i = 0; i < enchants.tagCount(); i++) {
                NBTTagCompound compound = enchants.getCompoundTagAt(i);
                short id = compound.getShort(ItemMetaKeys.ENCHANTMENT_ID);
                int level = 0xffff & compound.getShort(ItemMetaKeys.ENCHANTMENT_LEVEL);

                Enchantment enchantment = Enchantment.getById(id);
                if (enchantment != null) {
                    this.enchantments.put(enchantment, level);
                }
            }
        }

        if (source.hasKey(ItemMetaKeys.ATTRIBUTES, NbtTagNumbers.TAG_LIST)) {
            NBTTagList mods = source.getTagList(ItemMetaKeys.ATTRIBUTES, NbtTagNumbers.TAG_COMPOUND);
            for (int i = 0; i < mods.tagCount(); i++) {
                NBTTagCompound compound = mods.getCompoundTagAt(i);
                if (compound.hasNoTags()) {
                    continue;
                }

                AttributeModifier modifier = this.create(compound);
                if (modifier == null) {
                    continue;
                }

                String attributeName = compound.getString(ItemMetaKeys.ATTRIBUTES_IDENTIFIER);
                if (attributeName == null || attributeName.isEmpty()) {
                    continue;
                }

                Attribute attribute = this.findAttribute(attributeName);
                if (attribute == null) {
                    continue;
                }

                if (compound.hasKey(ItemMetaKeys.ATTRIBUTES_SLOT, NbtTagNumbers.TAG_STRING)) {
                    String slotName = compound.getString(ItemMetaKeys.ATTRIBUTES_SLOT);
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

        if (source.hasKey(ItemMetaKeys.REPAIR)) {
            this.repairCost = source.getInteger(ItemMetaKeys.REPAIR);
        }

        if (source.hasKey(ItemMetaKeys.HIDE_FLAGS)) {
            this.hideFlag = source.getInteger(ItemMetaKeys.HIDE_FLAGS);
        }

        if (source.hasKey(ItemMetaKeys.UNBREAKABLE)) {
            this.unbreakable = source.getBoolean(ItemMetaKeys.UNBREAKABLE);
        }

        if (source.hasKey(ItemMetaKeys.DAMAGE)) {
            this.damage = source.getInteger(ItemMetaKeys.DAMAGE);
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
        UUID uuid = new UUID(compound.getLong(ItemMetaKeys.ATTRIBUTES_UUID_HIGH), compound.getLong(ItemMetaKeys.ATTRIBUTES_UUID_LOW));

        try {
            AttributeModifier.Operation operation = AttributeModifier.Operation.values()[compound.getInteger(ItemMetaKeys.ATTRIBUTES_TYPE)];
            return new AttributeModifier(
                    uuid,
                    compound.getString(compound.getString(ItemMetaKeys.ATTRIBUTES_NAME)),
                    compound.getDouble(compound.getString(ItemMetaKeys.ATTRIBUTES_VALUE)),
                    operation
            );
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    public Set<ItemFlag> getItemFlags() {
        Set<ItemFlag> currentFlags = EnumSet.noneOf(ItemFlag.class);
        for (ItemFlag f : ItemFlag.values()) {
            if (hasItemFlag(f)) {
                currentFlags.add(f);
            }
        }

        return currentFlags;
    }

    public void addItemFlags(@NonNls ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.hideFlag |= this.getBitModifier(itemFlag);
        }
    }

    public void removeItemFlags(@NonNls ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.hideFlag &= ~this.getBitModifier(itemFlag);
        }
    }

    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        int bitModifier = this.getBitModifier(flag);
        return (this.hideFlag & bitModifier) == bitModifier;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public Multimap<Attribute, AttributeModifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Multimap<Attribute, AttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public List<TranslatableComponent> getLore() {
        return lore;
    }

    public void setLore(List<TranslatableComponent> lore) {
        this.lore = lore;
    }

    public TranslatableComponent getDisplayName() {
        return displayName;
    }

    public void setDisplayName(TranslatableComponent displayName) {
        this.displayName = displayName;
    }

    public TranslatableComponent getLocName() {
        return locName;
    }

    public void setLocName(TranslatableComponent locName) {
        this.locName = locName;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public NBTTagCompound getBlockData() {
        return blockData;
    }

    public void setBlockData(NBTTagCompound blockData) {
        this.blockData = blockData;
    }

    private byte getBitModifier(ItemFlag hideFlag) {
        return (byte) (1 << hideFlag.ordinal());
    }

    @NotNull
    private static String limit(@NotNull String s, int limit) {
        if (s.length() > limit) {
            s = s.substring(0, limit);
        }

        return s;
    }

    public NBTTagCompound write() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (this.displayName != null || this.locName != null || !this.lore.isEmpty()) {
            nbtTagCompound.setTag(ItemMetaKeys.DISPLAY, new NBTTagCompound());
            if (this.displayName != null) {
                nbtTagCompound.getCompoundTag(ItemMetaKeys.DISPLAY).setString(ItemMetaKeys.NAME, this.displayName.key());
            }

            if (this.locName != null) {
                nbtTagCompound.getCompoundTag(ItemMetaKeys.DISPLAY).setString(ItemMetaKeys.LOC_NAME, this.locName.key());
            }

            if (!this.lore.isEmpty()) {
                nbtTagCompound.getCompoundTag(ItemMetaKeys.DISPLAY).setTag(ItemMetaKeys.LORE, new NBTTagList());
                for (TranslatableComponent translatableComponent : this.lore) {
                    nbtTagCompound.getCompoundTag(ItemMetaKeys.DISPLAY)
                            .getTagList(ItemMetaKeys.LORE, NbtTagNumbers.TAG_STRING)
                            .appendTag(new NBTTagString(translatableComponent.key()));
                }
            }
        }

        if (this.customModelData != 0) {
            nbtTagCompound.setInteger(ItemMetaKeys.CUSTOM_MODEL_DATA, this.customModelData);
        }

        if (this.blockData != null) {
            nbtTagCompound.setTag(ItemMetaKeys.BLOCK_DATA, this.blockData);
        }

        nbtTagCompound.setBoolean(ItemMetaKeys.UNBREAKABLE, this.unbreakable);

        if (this.repairCost > 0) {
            nbtTagCompound.setInteger(ItemMetaKeys.REPAIR, this.repairCost);
        }

        if (this.hideFlag != 0) {
            nbtTagCompound.setInteger(ItemMetaKeys.HIDE_FLAGS, this.hideFlag);
        }

        if (this.damage > 0) {
            nbtTagCompound.setInteger(ItemMetaKeys.DAMAGE, this.damage);
        }

        if (!this.enchantments.isEmpty()) {
            nbtTagCompound.setTag(ItemMetaKeys.ENCHANTMENTS, new NBTTagList());
            for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : this.enchantments.entrySet()) {
                NBTTagCompound compound = new NBTTagCompound();

                compound.setShort(ItemMetaKeys.ENCHANTMENT_ID, enchantmentIntegerEntry.getKey().getId());
                compound.setShort(ItemMetaKeys.ENCHANTMENT_LEVEL, enchantmentIntegerEntry.getValue().shortValue());

                nbtTagCompound.getTagList(ItemMetaKeys.ENCHANTMENTS, NbtTagNumbers.TAG_COMPOUND).appendTag(compound);
            }
        }

        if (!this.modifiers.isEmpty()) {
            nbtTagCompound.setTag(ItemMetaKeys.ATTRIBUTES, new NBTTagList());
            for (Map.Entry<Attribute, AttributeModifier> entry : this.modifiers.entries()) {
                NBTTagCompound compound = new NBTTagCompound();

                compound.setLong(ItemMetaKeys.ATTRIBUTES_UUID_HIGH, entry.getValue().getUniqueId().getMostSignificantBits());
                compound.setLong(ItemMetaKeys.ATTRIBUTES_UUID_LOW, entry.getValue().getUniqueId().getLeastSignificantBits());
                compound.setInteger(ItemMetaKeys.ATTRIBUTES_TYPE, entry.getValue().getOperation().ordinal());
                compound.setString(ItemMetaKeys.ATTRIBUTES_NAME, entry.getValue().getName());
                compound.setDouble(ItemMetaKeys.ATTRIBUTES_VALUE, entry.getValue().getAmount());

                if (entry.getValue().getSlot() != null) {
                    compound.setString(ItemMetaKeys.ATTRIBUTES_SLOT, entry.getValue().getSlot().getSlotNameNms());
                }

                compound.setString(ItemMetaKeys.ATTRIBUTES_IDENTIFIER, entry.getKey().name().replaceFirst("_", ".").toLowerCase());
            }
        }

        for (Map.Entry<String, NBTBase> stringNBTBaseEntry : this.unhandled.entrySet()) {
            nbtTagCompound.setTag(stringNBTBaseEntry.getKey(), stringNBTBaseEntry.getValue());
        }

        return nbtTagCompound;
    }

    protected static final Collection<String> HANDLED = new ArrayList<>(Arrays.asList(
            ItemMetaKeys.DISPLAY,
            ItemMetaKeys.BLOCK_DATA,
            ItemMetaKeys.CUSTOM_MODEL_DATA,
            ItemMetaKeys.ENCHANTMENTS,
            ItemMetaKeys.ATTRIBUTES,
            ItemMetaKeys.REPAIR,
            ItemMetaKeys.HIDE_FLAGS,
            ItemMetaKeys.UNBREAKABLE,
            ItemMetaKeys.DAMAGE
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
