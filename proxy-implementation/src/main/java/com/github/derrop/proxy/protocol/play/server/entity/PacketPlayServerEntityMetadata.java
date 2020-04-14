package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.util.DataWatcher;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.io.IOException;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PacketPlayServerEntityMetadata extends DefinedPacket {

    private int entityId;
    private List<DataWatcher.WatchableObject> watchableObjects;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        try {
            this.watchableObjects = DataWatcher.readWatchedListFromByteBuf(buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        try {
            DataWatcher.writeWatchedListToByteBuf(this.watchableObjects, buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
