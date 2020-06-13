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
package com.github.derrop.proxy.api.connection.player;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.connection.player.inventory.PlayerInventory;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.util.Side;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import net.kyori.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Represents a player who's connection is being connected to somewhere else,
 * whether it be a remote or embedded server.
 */
public interface Player extends OfflinePlayer, Connection, CommandSender, Entity {

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
    void sendMessage(ChatMessageType position, Component... messages);

    /**
     * Send a message to the specified screen position of this player.
     *
     * @param position the screen position
     * @param message  the message to send
     */
    void sendMessage(ChatMessageType position, Component message);

    void sendActionBar(int units, Component... message);

    /**
     * Appends the given message to the action bar sent by the server
     * @param side the side where the message should be appended
     * @param message the message to be appended, when the supplier returns null, it won't be appended anymore
     */
    void appendActionBar(@NotNull Side side, @NotNull Supplier<String> message);

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

    int getVersion();

    void disableAutoReconnect();

    void enableAutoReconnect();

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
    void setTabHeader(Component header, Component footer);

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

    void sendBlockChange(BlockPos pos, int blockState);

    void sendBlockChange(BlockPos pos, Material material);

    PlayerInventory getInventory();

}
