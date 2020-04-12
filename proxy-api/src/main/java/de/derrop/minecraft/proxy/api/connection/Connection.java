package de.derrop.minecraft.proxy.api.connection;

import de.derrop.minecraft.proxy.api.chat.component.BaseComponent;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * A proxy connection is defined as a connection directly connected to a socket.
 * It should expose information about the remote peer, however not be specific
 * to a type of connection, whether server or player.
 */
public interface Connection extends PacketReceiver {

    /**
     * Gets the remote address of this connection.
     *
     * @return the remote address
     * @deprecated BungeeCord can accept connections via Unix domain sockets
     */
    @Deprecated
    InetSocketAddress getAddress();

    /**
     * Gets the remote address of this connection.
     *
     * @return the remote address
     */
    SocketAddress getSocketAddress();

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link ProxiedPlayer} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(String reason);

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link ProxiedPlayer} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(BaseComponent... reason);

    /**
     * Disconnects this end of the connection for the specified reason. If this
     * is an {@link ProxiedPlayer} the respective server connection will be
     * closed too.
     *
     * @param reason the reason shown to the player / sent to the server on
     *               disconnect
     */
    void disconnect(BaseComponent reason);

    /**
     * Gets whether this connection is currently open, ie: not disconnected, and
     * able to send / receive data.
     *
     * @return current connection status
     */
    boolean isConnected();
}
