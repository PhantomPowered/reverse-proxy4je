package com.github.derrop.proxy.protocol.play.server.inventory;

import com.github.derrop.proxy.connection.PacketUtil;
import com.github.derrop.proxy.connection.cache.InventoryItem;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerSetSlot extends DefinedPacket {

    private byte windowId;
    private int slot;
    private InventoryItem item;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.item = PacketUtil.readItem(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        PacketUtil.writeItem(buf, this.item);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SET_SLOT;
    }
}
