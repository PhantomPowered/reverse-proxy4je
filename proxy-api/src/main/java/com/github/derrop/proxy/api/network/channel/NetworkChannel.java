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
package com.github.derrop.proxy.api.network.channel;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.task.Task;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

public interface NetworkChannel extends AutoCloseable {

    default void write(@NotNull Packet packet) {
        this.write((Object) packet);
    }

    void write(@NotNull Object packet);

    @NotNull Task<Boolean> writeWithResult(@NotNull Object packet);

    void setProtocolState(@NotNull ProtocolState state);

    ProtocolState getProtocolState();

    @NotNull InetSocketAddress getAddress();

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

    @Nullable <T> T getProperty(String key);

    <T> void setProperty(String key, T value);

    void removeProperty(String key);

    void addOutgoingPacketListener(UUID key, Consumer<Packet> consumer);

    void removeOutgoingPacketListener(UUID key);

}
