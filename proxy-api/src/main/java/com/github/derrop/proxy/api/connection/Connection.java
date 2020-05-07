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
package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import net.kyori.text.Component;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public interface Connection extends PacketSender, NetworkChannel {

    /**
     * Gets the remote address of this connection.
     *
     * @return the remote address
     */
    @NotNull
    SocketAddress getSocketAddress();

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link Player} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    default void disconnect(@NotNull String reason) {
        this.disconnect(LegacyComponentSerializer.legacy().deserialize(reason));
    }

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link Player} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(@NotNull Component reason);

    /**
     * Gets whether this connection is currently open, ie: not disconnected, and
     * able to send / receive data.
     *
     * @return current connection status
     */
    boolean isConnected();

    void handleDisconnected(@NotNull ServiceConnection connection, @NotNull Component reason);
}
