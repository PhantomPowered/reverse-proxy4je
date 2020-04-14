package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public interface Connection extends PacketSender {

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
        this.disconnect(TextComponent.fromLegacyText(reason));
    }

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link Player} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(@NotNull BaseComponent... reason);

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link Player} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(@NotNull BaseComponent reason);

    /**
     * Gets whether this connection is currently open, ie: not disconnected, and
     * able to send / receive data.
     *
     * @return current connection status
     */
    boolean isConnected();

    void handleDisconnected(@NotNull ServiceConnection connection, @NotNull BaseComponent[] reason);
}
