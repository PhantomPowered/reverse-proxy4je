package com.github.derrop.proxy.network.channel;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;

public interface WrappedNetworkChannel extends NetworkChannel {

    NetworkChannel getWrappedNetworkChannel();
    
    @Override
    default void write(@NotNull Object packet) {
        this.getWrappedNetworkChannel().write(packet);
    }

    @Override
    default void setProtocolState(@NotNull ProtocolState state) {
        this.getWrappedNetworkChannel().setProtocolState(state);
    }

    @Override
    default @NotNull ProtocolState getProtocolState() {
        return this.getWrappedNetworkChannel().getProtocolState();
    }

    @Override
    default @NotNull SocketAddress getAddress() {
        return this.getWrappedNetworkChannel().getAddress();
    }

    @Override
    default void close(@Nullable Object goodbyeMessage) {
        this.getWrappedNetworkChannel().close(goodbyeMessage);
    }

    @Override
    default void delayedClose(@Nullable Packet goodbyeMessage) {
        this.getWrappedNetworkChannel().delayedClose(goodbyeMessage);
    }

    @Override
    default void setCompression(int compression) {
        this.getWrappedNetworkChannel().setCompression(compression);
    }

    @Override
    default boolean isClosed() {
        return this.getWrappedNetworkChannel().isClosed();
    }

    @Override
    default boolean isClosing() {
        return this.getWrappedNetworkChannel().isClosing();
    }

    @Override
    default boolean isConnected() {
        return this.getWrappedNetworkChannel().isConnected();
    }

    @Override
    default Channel getWrappedChannel() {
        return this.getWrappedNetworkChannel().getWrappedChannel();
    }
    
    @Override
    default <T> T getProperty(String key) {
        return this.getWrappedNetworkChannel().getProperty(key);
    }

    @Override
    default <T> void setProperty(String key, T value) {
        this.getWrappedNetworkChannel().setProperty(key, value);
    }
}
