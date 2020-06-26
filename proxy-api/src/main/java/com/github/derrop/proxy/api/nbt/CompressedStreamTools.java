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
package com.github.derrop.proxy.api.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CompressedStreamTools {

    public static NBTTagCompound read(DataInput dataInput, NBTSizeTracker nbtSizeTracker) throws IOException {
        NBTBase nbtbase = readNbtBase(dataInput, nbtSizeTracker);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound nbtTagCompound, DataOutput target) throws IOException {
        writeTag(nbtTagCompound, target);
    }

    private static void writeTag(NBTBase nbtBase, DataOutput target) throws IOException {
        target.writeByte(nbtBase.getId());

        if (nbtBase.getId() != 0) {
            target.writeUTF("");
            nbtBase.write(target);
        }
    }

    private static NBTBase readNbtBase(DataInput source, NBTSizeTracker nbtSizeTracker) throws IOException {
        byte b = source.readByte();
        if (b == 0) {
            return new NBTTagEnd();
        }

        source.readUTF();
        NBTBase nbtbase = NBTBase.createNewByType(b);

        if (nbtbase == null) {
            return null;
        }

        try {
            nbtbase.read(source, 0, nbtSizeTracker);
            return nbtbase;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
