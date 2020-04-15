package com.github.derrop.proxy.api.network.channel;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;

public interface NetworkChannel extends AutoCloseable {

    default void write(@NotNull Packet packet) {
        this.write((Object) packet);
    }

    void write(@NotNull Object packet);

    void setProtocolState(@NotNull ProtocolState state);

    @NotNull ProtocolState getProtocolState();

    @NotNull SocketAddress getAddress();

    void close(@Nullable Object goodbyeMessage);

    void delayedClose(@Nullable Packet goodbyeMessage);

    void setCompression(int compression);

    boolean isClosed();

    boolean isClosing();

    boolean isConnected();

    @Override
    default void close() {
        this.close(null);
    }

    Channel getWrappedChannel();
}
