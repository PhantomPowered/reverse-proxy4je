package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.util.PlayerPositionPacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class PacketPlayInPositionLook extends DefinedPacket {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<EnumFlags> flags;

    public PacketPlayInPositionLook(Location location, Set<EnumFlags> flags) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = PlayerPositionPacketUtil.getFixLocation(location.getYaw());
        this.pitch = PlayerPositionPacketUtil.getFixLocation(location.getPitch());
        this.flags = flags;
    }

    @Override
    public void read(@NotNull ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = EnumFlags.read(buf.readUnsignedByte());
    }

    @Override
    public void write(@NotNull ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(EnumFlags.write(this.flags));
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {

    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.POSITION_LOOK;
    }

    public enum EnumFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int id;

        EnumFlags(int id) {
            this.id = id;
        }

        private int id() {
            return 1 << this.id;
        }

        private boolean shouldAccept(int full) {
            return (full & this.id()) == this.id();
        }

        @NotNull
        public static Set<EnumFlags> read(int full) {
            Set<EnumFlags> set = EnumSet.noneOf(EnumFlags.class);
            for (EnumFlags flags : values()) {
                if (flags.shouldAccept(full)) {
                    set.add(flags);
                }
            }

            return set;
        }

        public static int write(@NotNull Set<EnumFlags> flags) {
            int i = 0;
            for (EnumFlags values : flags) {
                i |= values.id();
            }

            return i;
        }
    }
}
