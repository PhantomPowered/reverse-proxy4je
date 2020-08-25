/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.network.channel;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.task.Task;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

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
    @NotNull
    default Task<Boolean> writeWithResult(@NotNull Object packet) {
        return this.getWrappedNetworkChannel().writeWithResult(packet);
    }

    @Override
    default ProtocolState getProtocolState() {
        return this.getWrappedNetworkChannel().getProtocolState();
    }

    @Override
    default @NotNull InetSocketAddress getAddress() {
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

    @Override
    default void removeProperty(String key) {
        this.getWrappedNetworkChannel().removeProperty(key);
    }

    @Override
    default void addOutgoingPacketListener(UUID key, Consumer<Packet> consumer) {
        this.getWrappedNetworkChannel().addOutgoingPacketListener(key, consumer);
    }

    @Override
    default void removeOutgoingPacketListener(UUID key) {
        this.getWrappedNetworkChannel().removeOutgoingPacketListener(key);
    }
}
