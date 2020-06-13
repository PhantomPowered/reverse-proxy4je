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
package com.github.derrop.proxy.api.util.nbt;

import com.google.common.collect.Maps;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NBTTagCompound extends NBTBase {

    public final Map<String, NBTBase> tagMap = Maps.newConcurrentMap();

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        for (String s : this.tagMap.keySet()) {
            NBTBase nbtbase = this.tagMap.get(s);
            writeEntry(s, nbtbase, output);
        }

        output.writeByte(0);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
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

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public NBTTagCompound setTag(String key, NBTBase value) {
        this.tagMap.put(key, value);
        return this;
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public NBTTagCompound setByte(String key, byte value) {
        this.tagMap.put(key, new NBTTagByte(value));
        return this;
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public NBTTagCompound setShort(String key, short value) {
        this.tagMap.put(key, new NBTTagShort(value));
        return this;
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public NBTTagCompound setInteger(String key, int value) {
        this.tagMap.put(key, new NBTTagInt(value));
        return this;
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public NBTTagCompound setLong(String key, long value) {
        this.tagMap.put(key, new NBTTagLong(value));
        return this;
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public NBTTagCompound setFloat(String key, float value) {
        this.tagMap.put(key, new NBTTagFloat(value));
        return this;
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public NBTTagCompound setDouble(String key, double value) {
        this.tagMap.put(key, new NBTTagDouble(value));
        return this;
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public NBTTagCompound setString(String key, String value) {
        this.tagMap.put(key, new NBTTagString(value));
        return this;
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public NBTTagCompound setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new NBTTagByteArray(value));
        return this;
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public NBTTagCompound setIntArray(String key, int[] value) {
        this.tagMap.put(key, new NBTTagIntArray(value));
        return this;
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public NBTTagCompound setBoolean(String key, boolean value) {
        this.setByte(key, (byte) (value ? 1 : 0));
        return this;
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(String key) {
        return this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(String key) {
        NBTBase nbtbase = this.tagMap.get(key);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
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

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getByte();
        } catch (ClassCastException var3) {
            return (byte) 0;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getShort();
        } catch (ClassCastException var3) {
            return (short) 0;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getInt();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String key) {
        try {
            return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getLong();
        } catch (ClassCastException var3) {
            return 0L;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getFloat();
        } catch (ClassCastException var3) {
            return 0.0F;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive) this.tagMap.get(key)).getDouble();
        } catch (ClassCastException var3) {
            return 0.0D;
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String key) {
        try {
            return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
        } catch (ClassCastException var3) {
            return "";
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String key) {
        try {
            return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(key)).getByteArray();
        } catch (ClassCastException classcastexception) {
            classcastexception.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String key) {
        try {
            return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(key)).getIntArray();
        } catch (ClassCastException classcastexception) {
            classcastexception.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(String key) {
        try {
            return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound) this.tagMap.get(key);
        } catch (ClassCastException classcastexception) {
            classcastexception.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the NBTTagList object with the given name. Args: name, NBTBase type
     */
    public NBTTagList getTagList(String key, int type) {
        try {
            if (this.getTagId(key) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList nbttaglist = (NBTTagList) this.tagMap.get(key);
                return nbttaglist.tagCount() > 0 && nbttaglist.getTagType() != type ? new NBTTagList() : nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            classcastexception.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    /**
     * Remove the specified tag.
     */
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

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (String s : this.tagMap.keySet()) {
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) p_equals_1_;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

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

    /**
     * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
     * types of tags are overwritten from the given compound.
     */
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
