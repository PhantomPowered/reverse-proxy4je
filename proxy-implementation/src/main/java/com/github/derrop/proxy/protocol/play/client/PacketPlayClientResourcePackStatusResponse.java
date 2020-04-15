package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayClientResourcePackStatusResponse extends DefinedPacket {

    private String hash;
    private Action action;

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.hash = readString(buf);
        this.action = Action.values()[readVarInt(buf)];
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        writeString(this.hash, buf);
        writeVarInt(this.action.ordinal(), buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Play.RESOURCE_PACK_STATUS;
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED,
        ;
    }

}
