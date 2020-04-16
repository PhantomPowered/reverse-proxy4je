package com.github.derrop.proxy.api.network.wrapper;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class ProtoBuf extends ByteBuf implements Cloneable {

    public abstract int getProtocolVersion();

    public abstract void writeString(@NotNull String stringToWrite);

    public abstract void writeString(@NotNull String stringToWrite, short maxLength);

    @NotNull
    public abstract String readString();

    public abstract void writeArray(@NotNull byte[] bytes);

    public abstract void writeArray(@NotNull byte[] bytes, short limit);

    @NotNull
    public abstract byte[] readArray();

    @NotNull
    public abstract byte[] readArray(int limit);

    @NotNull
    public abstract byte[] toArray();

    public abstract void writeStringArray(@NotNull List<String> list);

    @NotNull
    public abstract List<String> readStringArray();

    public abstract int readVarInt();

    public abstract void writeVarInt(int value);

    public abstract long readVarLong();

    public abstract void writeVarLong(long value);

    @NotNull
    public abstract UUID readUniqueId();

    public abstract void writeUniqueId(@NotNull UUID uniqueId);

    @NotNull
    public abstract ProtoBuf clone();
}
