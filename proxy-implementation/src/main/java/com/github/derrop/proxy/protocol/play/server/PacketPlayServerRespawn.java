package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerRespawn extends DefinedPacket {

    private int dimension;
    private long seed;
    private short difficulty;
    private short gameMode;
    private String levelType;

    @Override
    public void read(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        dimension = buf.readInt();
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_15) {
            seed = buf.readLong();
        }
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_14) {
            difficulty = buf.readUnsignedByte();
        }
        gameMode = buf.readUnsignedByte();
        levelType = readString(buf);
    }

    @Override
    public void write(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        buf.writeInt(dimension);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_15) {
            buf.writeLong(seed);
        }
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_14) {
            buf.writeByte(difficulty);
        }
        buf.writeByte(gameMode);
        writeString(levelType, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.RESPAWN;
    }
}
