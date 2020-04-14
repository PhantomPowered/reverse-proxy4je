package com.github.derrop.proxy.api.entity.player;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.PendingConnection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.EntityLiving;
import com.github.derrop.proxy.api.util.ChatMessageType;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player who's connection is being connected to somewhere else,
 * whether it be a remote or embedded server.
 */
public interface Player extends OfflinePlayer, Connection, CommandSender, EntityLiving {

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

    @Nullable
    ServiceConnection getConnectedClient();

    void disableAutoReconnect();

    void enableAutoReconnect();

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
     * {@link ProvidedTitle#send(Player)}.
     *
     * @param providedTitle The title to send to the player.
     * @see ProvidedTitle
     */
    void sendTitle(ProvidedTitle providedTitle);
}
