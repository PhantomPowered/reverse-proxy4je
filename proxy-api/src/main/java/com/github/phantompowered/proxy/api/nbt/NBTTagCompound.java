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

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Contract;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NBTTagCompound extends NBTBase {

    public final Map<String, NBTBase> tagMap = Maps.newConcurrentMap();

    private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
        output.writeByte(data.getId());

        if (data.getId() != 0) {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }

    static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        NBTBase nbtbase = NBTBase.createNewByType(id);

        try {
            nbtbase.read(input, depth, sizeTracker);
            return nbtbase;
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
    }

    @Override
    public void write(DataOutput output) throws IOException {
        for (String s : this.tagMap.keySet()) {
            NBTBase nbtbase = this.tagMap.get(s);
            writeEntry(s, nbtbase, output);
        }

        output.writeByte(0);
    }

    @Override
    public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(384L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte b0;

            while ((b0 = readType(input, sizeTracker)) != 0) {
                String s = readKey(input, sizeTracker);
                sizeTracker.read(224 + 16 * s.length());
                NBTBase nbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);

                if (this.tagMap.put(s, nbtbase) != null) {
                    sizeTracker.read(288L);
                }
            }
        }
    }

    public Set<String> getKeySet() {
        return this.tagMap.keySet();
    }

    public Set<Map.Entry<String, NBTBase>> getEntrySet() {
        return this.tagMap.entrySet();
    }

    @Override
    public byte getId() {
        return (byte) 10;
    }

    public NBTTagCompound setTag(String key, NBTBase value) {
        this.tagMap.put(key, value);
        return this;
    }

    public NBTTagCompound setByte(String key, byte value) {
        this.tagMap.put(key, new NBTTagByte(value));
        return this;
    }

    public NBTTagCompound setShort(String key, short value) {
        this.tagMap.put(key, new NBTTagShort(value));
        return this;
    }

    public NBTTagCompound setInteger(String key, int value) {
        this.tagMap.put(key, new NBTTagInt(value));
        return this;
    }

    public NBTTagCompound setLong(String key, long value) {
        this.tagMap.put(key, new NBTTagLong(value));
        return this;
    }

    public NBTTagCompound setFloat(String key, float value) {
        this.tagMap.put(key, new NBTTagFloat(value));
        return this;
    }

    public NBTTagCompound setDouble(String key, double value) {
        this.tagMap.put(key, new NBTTagDouble(value));
        return this;
    }

    public NBTTagCompound setString(String key, String value) {
        this.tagMap.put(key, new NBTTagString(value));
        return this;
    }

    public NBTTagCompound setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new NBTTagByteArray(value));
        return this;
    }

    public NBTTagCompound setIntArray(String key, int[] value) {
        this.tagMap.put(key, new NBTTagIntArray(value));
        return this;
    }

    public NBTTagCompound setBoolean(String key, boolean value) {
        this.setByte(key, (byte) (value ? 1 : 0));
        return this;
    }

    public NBTBase getTag(String key) {
        return this.tagMap.get(key);
    }

    public byte getTagId(String key) {
        NBTBase nbtbase = this.tagMap.get(key);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    public boolean hasKey(String key, int type) {
        int i = this.getTagId(key);

        if (i == type) {
            return true;
        } else if (type != 99) {
            return false;
        } else {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
    }

    public byte getByte(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getByte();
        } catch (ClassCastException exception) {
            return (byte) 0;
        }
    }

    public short getShort(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getShort();
        } catch (ClassCastException exception) {
            return (short) 0;
        }
    }

    public int getInteger(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getInt();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public long getLong(String key) {
        try {
            return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getLong();
        } catch (ClassCastException exception) {
            return 0L;
        }
    }

    public float getFloat(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getFloat();
        } catch (ClassCastException exception) {
            return 0.0F;
        }
    }

    public double getDouble(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getDouble();
        } catch (ClassCastException exception) {
            return 0.0D;
        }
    }

    public String getString(String key) {
        try {
            return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
        } catch (ClassCastException exception) {
            return "";
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(key)).getByteArray();
        } catch (ClassCastException exception) {
            return null;
        }
    }

    public int[] getIntArray(String key) {
        try {
            return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(key)).getIntArray();
        } catch (ClassCastException exception) {
            return null;
        }
    }

    public NBTTagCompound getCompoundTag(String key) {
        try {
            return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound) this.tagMap.get(key);
        } catch (ClassCastException exception) {
            return null;
        }
    }

    public NBTTagList getTagList(String key, int type) {
        try {
            if (this.getTagId(key) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList nbttaglist = (NBTTagList) this.tagMap.get(key);
                return nbttaglist.tagCount() > 0 && nbttaglist.getTagType() != type ? new NBTTagList() : nbttaglist;
            }
        } catch (ClassCastException exception) {
            return null;
        }
    }

    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    public void removeTag(String key) {
        this.tagMap.remove(key);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");

        for (Entry<String, NBTBase> entry : this.tagMap.entrySet()) {
            if (stringbuilder.length() != 1) {
                stringbuilder.append(',');
            }

            stringbuilder.append(entry.getKey()).append(':').append(entry.getValue());
        }

        return stringbuilder.append('}').toString();
    }

    @Override
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    @Override
    public NBTBase copy() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (String s : this.tagMap.keySet()) {
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) other;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    public void merge(NBTTagCompound other) {
        for (String s : other.tagMap.keySet()) {
            NBTBase nbtbase = other.tagMap.get(s);

            if (nbtbase.getId() == 10) {
                if (this.hasKey(s, 10)) {
                    NBTTagCompound nbttagcompound = this.getCompoundTag(s);
                    nbttagcompound.merge((NBTTagCompound) nbtbase);
                } else {
                    this.setTag(s, nbtbase.copy());
                }
            } else {
                this.setTag(s, nbtbase.copy());
            }
        }
    }
}
