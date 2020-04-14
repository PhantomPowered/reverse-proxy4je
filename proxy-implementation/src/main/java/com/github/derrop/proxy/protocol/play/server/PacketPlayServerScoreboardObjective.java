package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.scoreboard.minecraft.criteria.IScoreObjectiveCriteria;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerScoreboardObjective extends DefinedPacket {

    private String name;
    private String value;
    private IScoreObjectiveCriteria.EnumRenderType type;
    /**
     * 0 to create, 1 to remove, 2 to update display text.
     */
    private byte action;

    /**
     * Destroy packet
     */
    public PacketPlayServerScoreboardObjective(String name) {
        this(name, null, null, (byte) 1);
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        name = readString(buf);
        action = buf.readByte();
        if (action == 0 || action == 2) {
            value = readString(buf);
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
                type = IScoreObjectiveCriteria.EnumRenderType.values()[readVarInt(buf)];
            } else {
                type = IScoreObjectiveCriteria.EnumRenderType.valueOf(readString(buf).toUpperCase());
            }
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(name, buf);
        buf.writeByte(action);
        if (action == 0 || action == 2) {
            writeString(value, buf);
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
                writeVarInt(type.ordinal(), buf);
            } else {
                writeString(type.toString().toLowerCase(), buf);
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public enum HealthDisplay {

        INTEGER, HEARTS;

        @Override
        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }

        public static HealthDisplay fromString(String s) {
            return valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
