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
package com.github.derrop.proxy.util;

import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.util.serialize.SerializableObject;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataWatcher {

    public DataWatcher() {
    }

    public DataWatcher(@NotNull Collection<SerializableObject> objects) {
        this.serializableObjects.addAll(objects);
    }

    private final Collection<SerializableObject> serializableObjects = new CopyOnWriteArrayList<>();

    public <T> void set(int i, @NotNull T t) {
        int dataType = SerializableObject.getDataType(t.getClass());

        Preconditions.checkArgument(dataType != -1, "Unknown data type " + t.getClass().getName());
        Preconditions.checkArgument(i <= 31, "Data value is too big with " + i + " max is 31");

        this.remove(i);
        this.serializableObjects.add(new SerializableObject(dataType, i, t));
    }

    public Byte getByte(int i) {
        return (Byte) this.get(i, (byte) 0);
    }

    public Short getShort(int i) {
        return (Short) this.get(i, (short) 0);
    }

    public Integer getInt(int i) {
        return (Integer) this.get(i, 0);
    }

    public Float getFloat(int i) {
        return (Float) this.get(i, 0f);
    }

    public String getString(int i) {
        return (String) this.get(i, null);
    }

    public ItemStack getItemStack(int i) {
        return (ItemStack) this.get(i, null);
    }

    public Location getLocation(int i) {
        return (Location) this.get(i, null);
    }

    public Rotations getRotations(int i) {
        return (Rotations) this.get(i, null);
    }

    public void remove(int id) {
        serializableObjects.removeIf(serializableObject -> serializableObject.getId() == id);
    }

    private Object get(int i, Object def) {
        for (SerializableObject serializableObject : this.serializableObjects) {
            if (serializableObject.getId() == i && serializableObject.getValue().isPresent()) {
                return serializableObject.getValue().get();
            }
        }

        return def;
    }
}
