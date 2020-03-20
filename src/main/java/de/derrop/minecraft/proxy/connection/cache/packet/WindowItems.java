package de.derrop.minecraft.proxy.connection.cache.packet;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import de.derrop.minecraft.proxy.connection.cache.InventoryItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WindowItems extends DefinedPacket {

    private byte windowId;
    private InventoryItem[] items;

    @Override
    public void read(ByteBuf buf) {
        this.windowId = buf.readByte();
        this.items = new InventoryItem[buf.readShort()];
        for (int i = 0; i < this.items.length; i++) {
            boolean present = buf.readBoolean();
            try {
                this.items[i] = present ?
                        new InventoryItem(true, DefinedPacket.readVarInt(buf), buf.readByte(), (CompoundTag) new NBTInputStream(new ByteBufInputStream(buf)).readTag()) :
                        InventoryItem.NONE;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.items.length);
        for (InventoryItem item : this.items) {
            buf.writeBoolean(item.isPresent());
            if (item.isPresent()) {
                DefinedPacket.writeVarInt(item.getItemId(), buf);
                buf.writeByte(item.getCount());
                try (NBTOutputStream outputStream = new NBTOutputStream(new ByteBufOutputStream(buf))) {
                    outputStream.writeTag(item.getNbt());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

}
