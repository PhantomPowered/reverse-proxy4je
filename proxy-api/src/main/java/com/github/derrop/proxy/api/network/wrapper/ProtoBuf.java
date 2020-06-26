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
package com.github.derrop.proxy.api.network.wrapper;

import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class ProtoBuf extends ByteBuf implements Cloneable {

    public abstract int getProtocolVersion();

    public abstract void writeString(@NotNull String stringToWrite);

    public abstract void writeString(@NotNull String stringToWrite, short maxLength);

    @NotNull
    public abstract String readString();

    public abstract void writeArray(@NotNull byte[] bytes);

    public abstract void writeArray(@NotNull byte[] bytes, short limit);

    @NotNull
    public abstract byte[] readArray();

    @NotNull
    public abstract byte[] readArray(int limit);

    @NotNull
    public abstract byte[] toArray();

    public abstract void writeStringArray(@NotNull List<String> list);

    @NotNull
    public abstract List<String> readStringArray();

    public abstract int readVarInt();

    public abstract void writeVarInt(int value);

    public abstract long readVarLong();

    public abstract void writeVarLong(long value);

    public abstract ItemStack readItemStack();

    public abstract void writeItemStack(ItemStack itemStack);

    public abstract NBTTagCompound readNBTTagCompound();

    public abstract void writeNBTTagCompound(NBTTagCompound nbt);

    public abstract Location readLocation();

    public abstract void writeLocation(Location pos);

    @NotNull
    public abstract UUID readUniqueId();

    public abstract void writeUniqueId(@NotNull UUID uniqueId);

    @Override
    @NotNull
    public abstract ProtoBuf clone();
}
