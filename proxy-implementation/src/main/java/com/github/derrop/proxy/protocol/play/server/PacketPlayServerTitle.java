package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerTitle extends DefinedPacket {

    private Action action;

    // TITLE & SUBTITLE
    private String text;

    // TIMES
    private int fadeIn;
    private int stay;
    private int fadeOut;

    @Override
    public void read(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = readVarInt(buf);

        // If we're working on 1.10 or lower, increment the value of the index so we pull out the correct value.
        if (protocolVersion <= ProtocolConstants.MINECRAFT_1_10 && index >= 2) {
            index++;
        }

        action = Action.values()[index];
        switch (action) {
            case TITLE:
            case SUBTITLE:
                text = readString(buf);
                break;
            case TIMES:
                fadeIn = buf.readInt();
                stay = buf.readInt();
                fadeOut = buf.readInt();
                break;
        }
    }

    @Override
    public void write(@NotNull ByteBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = action.ordinal();

        // If we're working on 1.10 or lower, increment the value of the index so we pull out the correct value.
        if (protocolVersion <= ProtocolConstants.MINECRAFT_1_10 && index >= 2) {
            index--;
        }

        writeVarInt(index, buf);
        switch (action) {
            case TITLE:
            case SUBTITLE:
                writeString(text, buf);
                break;
            case TIMES:
                buf.writeInt(fadeIn);
                buf.writeInt(stay);
                buf.writeInt(fadeOut);
                break;
        }
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TITLE;
    }

    public enum Action {

        TITLE,
        SUBTITLE,
        PLACEHOLDER,
        TIMES,
        CLEAR,
        RESET
    }
}
