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
package com.github.derrop.proxy.item.util;

import com.github.derrop.proxy.api.nbt.NBTTagCompound;
import com.github.derrop.proxy.api.nbt.NBTTagList;
import com.github.derrop.proxy.item.ProxyItemMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.UUID;

public final class GameProfileSerializer {

    @Nullable
    public static GameProfile deserialize(@NotNull NBTTagCompound compound) {
        String name = null;
        String stringUniqueId = null;

        if (compound.hasKey(GameProfilePropertyKeys.NAME, ProxyItemMeta.NbtTagNumbers.TAG_STRING)) {
            name = compound.getString(GameProfilePropertyKeys.NAME);
        }

        if (compound.hasKey(GameProfilePropertyKeys.ID, ProxyItemMeta.NbtTagNumbers.TAG_STRING)) {
            stringUniqueId = compound.getString(GameProfilePropertyKeys.ID);
        }

        if (name == null || name.isEmpty() || stringUniqueId == null || stringUniqueId.isEmpty()) {
            return null;
        } else {
            UUID uniqueId;

            try {
                uniqueId = UUID.fromString(stringUniqueId);
            } catch (Throwable throwable) {
                uniqueId = null;
            }

            GameProfile gameProfile = new GameProfile(uniqueId, name);
            if (compound.hasKey(GameProfilePropertyKeys.PROPERTIES, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND)) {
                NBTTagCompound properties = compound.getCompoundTag(GameProfilePropertyKeys.PROPERTIES);

                for (String s : properties.getKeySet()) {
                    NBTTagList list = properties.getTagList(s, ProxyItemMeta.NbtTagNumbers.TAG_COMPOUND);

                    for (int i = 0; i < list.tagCount(); ++i) {
                        NBTTagCompound property = list.getCompoundTagAt(i);
                        String value = property.getString(GameProfilePropertyKeys.VALUE);

                        if (property.hasKey(GameProfilePropertyKeys.SIGNATURE, ProxyItemMeta.NbtTagNumbers.TAG_STRING)) {
                            gameProfile.getProperties().put(s, new Property(s, value, property.getString(GameProfilePropertyKeys.SIGNATURE)));
                        } else {
                            gameProfile.getProperties().put(s, new Property(s, value));
                        }
                    }
                }
            }

            return gameProfile;
        }
    }

    public static void serialize(@NotNull NBTTagCompound compound, @NotNull GameProfile gameProfile) {
        if (gameProfile.getName() != null && !gameProfile.getName().isEmpty()) {
            compound.setString(GameProfilePropertyKeys.NAME, gameProfile.getName());
        }

        if (gameProfile.getId() != null) {
            compound.setString(GameProfilePropertyKeys.ID, gameProfile.getId().toString());
        }

        if (!gameProfile.getProperties().isEmpty()) {
            NBTTagCompound properties = new NBTTagCompound();

            for (String s : gameProfile.getProperties().keySet()) {
                NBTTagList list = new NBTTagList();
                NBTTagCompound property;

                for (Iterator<Property> var6 = gameProfile.getProperties().get(s).iterator(); var6.hasNext(); list.appendTag(property)) {
                    Property authlibProperty = var6.next();
                    property = new NBTTagCompound();

                    property.setString(GameProfilePropertyKeys.VALUE, authlibProperty.getValue());
                    if (authlibProperty.hasSignature()) {
                        property.setString(GameProfilePropertyKeys.SIGNATURE, authlibProperty.getSignature());
                    }
                }

                properties.setTag(s, list);
            }

            compound.setTag(GameProfilePropertyKeys.PROPERTIES, properties);
        }
    }

    public interface GameProfilePropertyKeys {

        String NAME = "Name";
        String ID = "Id";
        String VALUE = "Value";
        String SIGNATURE = "Signature";
        String PROPERTIES = "Properties";
    }
}
