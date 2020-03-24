package de.derrop.minecraft.proxy.connection.cache.packet.entity.spawn;

import de.derrop.minecraft.proxy.util.DataWatcher;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SpawnPlayer extends DefinedPacket implements PositionedPacket {

    private int entityId;
    private UUID playerId;
    private int x;
    private int y;
    private int z;
    private byte yaw;
    private byte pitch;
    private short currentItem;
    private List<DataWatcher.WatchableObject> watchableObjects;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.playerId = readUUID(buf);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.currentItem = buf.readShort();
        try {
            this.watchableObjects = DataWatcher.readWatchedListFromByteBuf(buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        writeUUID(this.playerId, buf);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeShort(this.currentItem);
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
