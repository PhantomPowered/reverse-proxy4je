package de.derrop.minecraft.proxy.connection.cache.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class JoinGame extends DefinedPacket {

    private int entityId;
    private boolean hardcoreMode;
    private int gameType;
    private int dimension;
    private int difficulty;
    private int maxPlayers;
    private String worldType;
    private boolean reducedDebugInfo;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = buf.readInt();
        int i = buf.readUnsignedByte();
        this.hardcoreMode = (i & 8) == 8;
        i = i & -9;
        this.gameType = i;
        this.dimension = buf.readByte();
        this.difficulty = buf.readUnsignedByte();
        this.maxPlayers = buf.readUnsignedByte();
        this.worldType = readString(buf);
        this.reducedDebugInfo = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.entityId);
        int i = this.gameType;
        if (this.hardcoreMode) {
            i |= 8;
        }

        buf.writeByte(i);
        buf.writeByte(this.dimension);
        buf.writeByte(this.difficulty);
        buf.writeByte(this.maxPlayers);
        writeString(this.worldType, buf);
        buf.writeBoolean(this.reducedDebugInfo);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
    }
}
