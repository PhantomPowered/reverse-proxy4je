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
package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.api.util.nbt.CompressedStreamTools;
import com.github.derrop.proxy.api.util.nbt.NBTSizeTracker;
import com.github.derrop.proxy.api.util.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class PacketUtil {

    public static InventoryItem readItem(ByteBuf buf) {
        int itemId = buf.readShort();
        if (itemId >= 0) {
            int amount = buf.readByte();
            int meta = buf.readShort();

            return new InventoryItem(itemId, amount, meta, readNBTTagCompound(buf));
        }
        return InventoryItem.NONE;
    }

    public static void writeItem(ByteBuf buf, InventoryItem item) {
        if (item.getItemId() <= 0) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(item.getItemId());
            buf.writeByte(item.getAmount());
            buf.writeShort(item.getMeta());

            writeNBTTagCompound(buf, item.getNbt());
        }
    }

    public static NBTTagCompound readNBTTagCompound(ByteBuf buf) {
        int index = buf.readerIndex();
        byte tagAvailable = buf.readByte();

        NBTTagCompound tag = null;
        if (tagAvailable != 0) {
            buf.readerIndex(index);
            try {
                tag = CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(Integer.MAX_VALUE));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return tag;
    }

    public static void writeNBTTagCompound(ByteBuf buf, NBTTagCompound tagCompound) {
        if (tagCompound != null) {
            try {
                CompressedStreamTools.write(tagCompound, new ByteBufOutputStream(buf));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            buf.writeByte(0);
        }
    }

}
