package de.derrop.minecraft.proxy.connection.cache.packet;

import de.derrop.minecraft.proxy.util.DataWatcher;
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
public class SpawnMob extends DefinedPacket implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int type;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private List<DataWatcher.WatchableObject> watcher;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.type = buf.readByte() & 255;
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.headPitch = buf.readByte();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
        try {
            this.watcher = DataWatcher.readWatchedListFromByteBuf(buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.type & 255);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeByte(this.headPitch);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
        try {
            DataWatcher.writeWatchedListToByteBuf(this.watcher, buf);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
