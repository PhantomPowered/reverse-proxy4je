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

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;

/**
 * Represents a user attempting to log into the proxy.
 */
public interface PendingConnection extends Connection {

    /**
     * Get the requested username.
     *
     * @return the requested username, or null if not set
     */
    String getName();

    /**
     * Get the numerical client version of the player attempting to log in.
     *
     * @return the protocol version of the remote client
     */
    int getVersion();

    /**
     * Get the requested virtual host that the client tried to connect to.
     *
     * @return request virtual host or null if invalid / not specified.
     */
    InetSocketAddress getVirtualHost();

    /**
     * Get this connection's UUID, if set.
     *
     * @return the UUID
     */
    UUID getUniqueId();

    /**
     * Set the connection's uuid
     *
     * @param uuid connection UUID
     */
    void setUniqueId(UUID uuid);

    /**
     * Get this connection's online mode.
     * <br>
     * See {@link #setOnlineMode(boolean)} for a description of how this option
     * works.
     *
     * @return the online mode
     */
    boolean isOnlineMode();

    /**
     * Set this connection's online mode.
     * <br>
     * May be called only during the PlayerHandshakeEvent to set the online mode
     * configuration setting for this connection only (i.e. whether or not the
     * client will be treated as if it is connecting to an online mode server).
     *
     * @param onlineMode status
     */
    void setOnlineMode(boolean onlineMode);

    /**
     * Check if the client is using the older unsupported Minecraft protocol
     * used by Minecraft clients older than 1.7.
     *
     * @return Whether the client is using a legacy client.
     */
    boolean isLegacy();
}
