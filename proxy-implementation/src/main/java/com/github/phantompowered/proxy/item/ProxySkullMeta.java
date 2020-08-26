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
package com.github.phantompowered.proxy.item;

import com.github.phantompowered.proxy.api.item.SkullMeta;
import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import com.github.phantompowered.proxy.item.util.GameProfileSerializer;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxySkullMeta extends ProxyItemMeta implements SkullMeta {

    static {
        HANDLED.add(SkullMetaKeys.SKULL_OWNER);
    }

    private GameProfile gameProfile;

    public ProxySkullMeta(@NotNull NBTTagCompound source) {
        super(source);

        if (source.hasKey(SkullMetaKeys.SKULL_OWNER, NbtTagNumbers.TAG_COMPOUND)) {
            this.gameProfile = GameProfileSerializer.deserialize(source.getCompoundTag(SkullMetaKeys.SKULL_OWNER));
        } else if (source.hasKey(SkullMetaKeys.SKULL_OWNER, NbtTagNumbers.TAG_STRING) && !source.getString(SkullMetaKeys.SKULL_OWNER).isEmpty()) {
            this.gameProfile = new GameProfile(null, source.getString(SkullMetaKeys.SKULL_OWNER));
        }
    }

    @Override
    public @Nullable GameProfile getOwner() {
        return this.gameProfile;
    }

    @Override
    public void setOwner(@Nullable GameProfile owner) {
        this.gameProfile = owner;
    }

    @Override
    public boolean hasOwner() {
        return this.gameProfile != null;
    }

    @Override
    public @NotNull NBTTagCompound write() {
        NBTTagCompound compound = super.write();

        if (this.gameProfile != null) {
            NBTTagCompound owner = new NBTTagCompound();
            GameProfileSerializer.serialize(owner, this.gameProfile);
            compound.setTag(SkullMetaKeys.SKULL_OWNER, owner);
        }

        return compound;
    }

    public interface SkullMetaKeys {

        String SKULL_OWNER = "SkullOwner";
    }
}
