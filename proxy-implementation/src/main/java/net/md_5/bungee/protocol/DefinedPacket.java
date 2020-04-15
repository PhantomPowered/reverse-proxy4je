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

    public static int[] readVarIntArray(ByteBuf buf) {
        return ByteBufUtils.readVarIntArray(buf);
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

    public static int readVarShort(ByteBuf buf) {
        return ByteBufUtils.readVarShort(buf);
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        ByteBufUtils.writeVarShort(buf, toWrite);
    }

    public static void writeUUID(UUID value, ByteBuf output) {
        ByteBufUtils.writeUUID(value, output);
    }

    public static UUID readUUID(ByteBuf input) {
        return ByteBufUtils.readUUID(input);
    }

    public void read(@NotNull ByteBuf buf) {
        throw new UnsupportedOperationException("Packet must implement read method");
    }

    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        read(buf);
    }

    public void write(@NotNull ByteBuf buf) {
        throw new UnsupportedOperationException("Packet must implement write method");
    }

    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        write(buf);
    }

    public abstract void handle(AbstractPacketHandler handler) throws Exception;
}
