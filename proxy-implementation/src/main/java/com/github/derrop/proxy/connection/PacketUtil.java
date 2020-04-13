package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.util.nbt.CompressedStreamTools;
import com.github.derrop.proxy.util.nbt.NBTSizeTracker;
import com.github.derrop.proxy.util.nbt.NBTTagCompound;
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
