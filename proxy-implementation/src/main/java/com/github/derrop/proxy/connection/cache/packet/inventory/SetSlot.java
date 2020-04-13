package com.github.derrop.proxy.connection.cache.packet.inventory;

import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
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
public class SetSlot extends DefinedPacket {

    private byte windowId;
    private int slot;
    private InventoryItem item;

    @Override
    public void read(ByteBuf buf) {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.item = PacketUtil.readItem(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        PacketUtil.writeItem(buf, this.item);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }

}
