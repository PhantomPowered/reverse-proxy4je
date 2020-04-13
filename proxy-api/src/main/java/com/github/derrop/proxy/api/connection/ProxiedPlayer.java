package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.util.ChatMessageType;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player who's connection is being connected to somewhere else,
 * whether it be a remote or embedded server.
 */
public interface ProxiedPlayer extends Connection, CommandSender {

    /**
     * Represents the player's chat state.
     */
    enum ChatMode {

        /**
         * The player will see all chat.
         */
        SHOWN,
        /**
         * The player will only see everything except messages marked as chat.
         */
        COMMANDS_ONLY,
        /**
         * The chat is completely disabled, the player won't see anything.
         */
        HIDDEN

    }

    enum MainHand {

        LEFT,
        RIGHT
    }

    Proxy getProxy();

    /**
     * Gets this player's display name.
     *
     * @return the players current display name
     */
    String getDisplayName();

    /**
     * Sets this players display name to be used as their nametag and tab list
     * name.
     *
     * @param name the name to set
     */
    void setDisplayName(String name);

    /**
     * Send a message to the specified screen position of this player.
     *
     * @param position the screen position
     * @param message  the message to send
     */
    void sendMessage(ChatMessageType position, BaseComponent... message);

    /**
     * Send a message to the specified screen position of this player.
     *
     * @param position the screen position
     * @param message  the message to send
     */
    void sendMessage(ChatMessageType position, BaseComponent message);

    void sendActionBar(int units, BaseComponent... message);

    /**
     * Send a plugin message to this player.
     * <p>
     * In recent Minecraft versions channel names must contain a colon separator
     * and consist of [a-z0-9/._-]. This will be enforced in a future version.
     * The "BungeeCord" channel is an exception and may only take this form.
     *
     * @param channel the channel to send this data via
     * @param data    the data to send
     */
    void sendData(String channel, byte[] data);

    void useClientSafe(ServiceConnection connection);

    void useClient(ServiceConnection connection);

    ServiceConnection getConnectedClient();

    void disableAutoReconnect();

    void enableAutoReconnect();

    @NotNull String getName();

    /**
     * Get the pending connection that belongs to this player.
     *
     * @return the pending connection that this player used
     */
    PendingConnection getPendingConnection();

    /**
     * Make this player chat (say something), to the server he is currently on.
     *
     * @param message the message to say
     */
    void chat(String message);

    /**
     * Get this connection's UUID, if set.
     *
     * @return the UUID
     * @deprecated In favour of {@link #getUniqueId()}
     */
    @Deprecated
    String getUUID();

    /**
     * Get this connection's UUID, if set.
     *
     * @return the UUID
     */
    @NotNull UUID getUniqueId();

    /**
     * Set the header and footer displayed in the tab player list.
     *
     * @param header The header for the tab player list, null to clear it.
     * @param footer The footer for the tab player list, null to clear it.
     */
    void setTabHeader(BaseComponent header, BaseComponent footer);

    /**
     * Set the header and footer displayed in the tab player list.
     *
     * @param header The header for the tab player list, null to clear it.
     * @param footer The footer for the tab player list, null to clear it.
     */
    void setTabHeader(BaseComponent[] header, BaseComponent[] footer);

    /**
     * Clears the header and footer displayed in the tab player list.
     */
    void resetTabHeader();

    /**
     * Sends a {@link ProvidedTitle} to this player. This is the same as calling
     * {@link ProvidedTitle#send(ProxiedPlayer)}.
     *
     * @param providedTitle The title to send to the player.
     * @see ProvidedTitle
     */
    void sendTitle(ProvidedTitle providedTitle);

    Scoreboard getScoreboard();

}
