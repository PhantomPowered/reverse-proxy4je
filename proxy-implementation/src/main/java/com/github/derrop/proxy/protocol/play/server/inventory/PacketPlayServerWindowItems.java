package com.github.derrop.proxy.protocol.play.server.inventory;

import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerWindowItems extends DefinedPacket {

    private byte windowId;
    private InventoryItem[] items;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.windowId = buf.readByte();
        this.items = new InventoryItem[buf.readShort()];
        for (int i = 0; i < this.items.length; i++) {
            this.items[i] = PacketUtil.readItem(buf);
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.items.length);
        for (InventoryItem item : this.items) {
            PacketUtil.writeItem(buf, item);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.WINDOW_ITEMS;
    }
}
