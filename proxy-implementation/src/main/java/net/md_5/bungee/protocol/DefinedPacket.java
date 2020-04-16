package net.md_5.bungee.protocol;

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class DefinedPacket implements Packet {

    public static void writeString(String s, ByteBuf buf) {
        ByteBufUtils.writeString(s, buf);
    }

    public static String readString(ByteBuf buf) {
        return ByteBufUtils.readString(buf);
    }

    public static void writeArray(byte[] b, ByteBuf buf) {
        ByteBufUtils.writeArray(b, buf);
    }

    public static void writeArrayNoLimit(byte[] b, ByteBuf buf) {
        ByteBufUtils.writeArrayNoLimit(b, buf);
    }

    public static byte[] toArray(ByteBuf buf) {
        return ByteBufUtils.toArray(buf);
    }

    public static byte[] readArray(ByteBuf buf) {
        return ByteBufUtils.readArray(buf);
    }

    public static byte[] readArray(ByteBuf buf, int limit) {
        return ByteBufUtils.readArray(buf, limit);
    }

    public static void writeStringArray(List<String> s, ByteBuf buf) {
        ByteBufUtils.writeStringArray(s, buf);
    }

    public static List<String> readStringArray(ByteBuf buf) {
        return ByteBufUtils.readStringArray(buf);
    }

    public static int readVarInt(ByteBuf input) {
        return ByteBufUtils.readVarInt(input);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        return ByteBufUtils.readVarInt(input, maxBytes);
    }

    public static void writeVarInt(int value, ByteBuf output) {
        ByteBufUtils.writeVarInt(value, output);
    }

    public static long readVarLong(ByteBuf buf) {
        return ByteBufUtils.readVarLong(buf);
    }

    public static void writeVarLong(long value, ByteBuf buf) {
        ByteBufUtils.writeVarLong(value, buf);
    }

    public static void writeUUID(UUID value, ByteBuf output) {
        ByteBufUtils.writeUUID(value, output);
    }

    public static UUID readUUID(ByteBuf input) {
        return ByteBufUtils.readUUID(input);
    }

    public void read(@NotNull ByteBuf buf) {
        throw new UnsupportedOperationException("Packet (" + this.getClass().getName() + ") must implement read method");
    }

    public void write(@NotNull ByteBuf buf) {
        throw new UnsupportedOperationException("Packet (" + this.getClass().getName() + ") must implement write method");
    }

}
