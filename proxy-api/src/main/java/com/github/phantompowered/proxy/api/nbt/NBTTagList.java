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
package com.github.phantompowered.proxy.api.nbt;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class NBTTagList extends NBTBase {

    private List<NBTBase> tagList = Lists.newArrayList();
    private byte tagType = 0;

    @Override
    public void write(DataOutput output) throws IOException {
        if (!this.tagList.isEmpty()) {
            this.tagType = this.tagList.get(0).getId();
        } else {
            this.tagType = 0;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (NBTBase nbtBase : this.tagList) {
            nbtBase.write(output);
        }
    }

    @Override
    public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(296L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagType = input.readByte();
            int i = input.readInt();

            if (this.tagType == 0 && i > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                sizeTracker.read(32L * (long) i);
                this.tagList = Lists.newArrayListWithCapacity(i);

                for (int j = 0; j < i; ++j) {
                    NBTBase nbtbase = NBTBase.createNewByType(this.tagType);
                    nbtbase.read(input, depth + 1, sizeTracker);
                    this.tagList.add(nbtbase);
                }
            }
        }
    }

    @Override
    public byte getId() {
        return (byte) 9;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[");
        for (int i = 0; i < this.tagList.size(); ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(i).append(':').append(this.tagList.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    public void appendTag(NBTBase nbt) {
        if (nbt.getId() != 0) {
            if (this.tagType == 0) {
                this.tagType = nbt.getId();
            } else if (this.tagType != nbt.getId()) {
                return;
            }

            this.tagList.add(nbt);
        }
    }

    public void set(int idx, NBTBase nbt) {
        if (nbt.getId() != 0) {
            if (idx >= 0 && idx < this.tagList.size()) {
                if (this.tagType == 0) {
                    this.tagType = nbt.getId();
                } else if (this.tagType != nbt.getId()) {
                    return;
                }

                this.tagList.set(idx, nbt);
            }
        }
    }

    public NBTBase removeTag(int i) {
        return this.tagList.remove(i);
    }

    @Override
    public boolean hasNoTags() {
        return this.tagList.isEmpty();
    }

    public NBTTagCompound getCompoundTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 10 ? (NBTTagCompound) nbtbase : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }

    public int[] getIntArrayAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 11 ? ((NBTTagIntArray) nbtbase).getIntArray() : new int[0];
        } else {
            return new int[0];
        }
    }

    public double getDoubleAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 6 ? ((NBTTagDouble) nbtbase).getDouble() : 0.0D;
        } else {
            return 0.0D;
        }
    }

    public float getFloatAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 5 ? ((NBTTagFloat) nbtbase).getFloat() : 0.0F;
        } else {
            return 0.0F;
        }
    }

    public String getStringTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public NBTBase get(int idx) {
        return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new NBTTagEnd();
    }

    public int tagCount() {
        return this.tagList.size();
    }

    @Override
    public NBTBase copy() {
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.tagType = this.tagType;

        for (NBTBase nbtbase : this.tagList) {
            NBTBase nbtbase1 = nbtbase.copy();
            nbttaglist.tagList.add(nbtbase1);
        }

        return nbttaglist;
    }

    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagList nbttaglist = (NBTTagList) other;
            if (this.tagType == nbttaglist.tagType) {
                return this.tagList.equals(nbttaglist.tagList);
            }
        }

        return false;
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }

    public int getTagType() {
        return this.tagType;
    }
}
