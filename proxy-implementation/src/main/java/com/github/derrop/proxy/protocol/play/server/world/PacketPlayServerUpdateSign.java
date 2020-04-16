package com.github.derrop.proxy.protocol.play.server.world;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerUpdateSign implements Packet {

    private BlockPos pos;
    private BaseComponent[][] lines;

    public PacketPlayServerUpdateSign(BlockPos pos, BaseComponent[][] lines) {
        this.pos = pos;
        this.lines = lines;
    }

    public PacketPlayServerUpdateSign() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.UPDATE_SIGN;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BaseComponent[][] getLines() {
        return this.lines;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.pos = BlockPos.fromLong(protoBuf.readLong());
        this.lines = new BaseComponent[4][];

        for (int i = 0; i < 4; i++) {
            this.lines[i] = ComponentSerializer.parse(protoBuf.readString());
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeLong(this.pos.toLong());

        for (int i = 0; i < 4; i++) {
            protoBuf.writeString(ComponentSerializer.toString(this.lines[i]));
        }
    }

    public String toString() {
        return "PacketPlayServerUpdateSign(pos=" + this.getPos() + ", lines=" + java.util.Arrays.deepToString(this.getLines()) + ")";
    }
}
