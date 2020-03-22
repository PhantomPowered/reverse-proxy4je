package de.derrop.minecraft.proxy.connection.cache.packet.inventory;

import de.derrop.minecraft.proxy.connection.PacketUtil;
import de.derrop.minecraft.proxy.connection.cache.InventoryItem;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

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
            this.items[i] = PacketUtil.readItem(buf);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.items.length);
        for (InventoryItem item : this.items) {
            PacketUtil.writeItem(buf, item);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

}
