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
package com.github.phantompowered.proxy.api.connection;

import com.github.phantompowered.proxy.api.block.BlockAccess;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.location.BlockingObject;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.PlayerAbilities;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.scoreboard.Scoreboard;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.task.Task;
import com.mojang.authlib.UserAuthentication;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ServiceConnection extends InteractiveServiceConnection, Connection, AutoCloseable {

    @NotNull
    ServiceRegistry getServiceRegistry();

    @Nullable
    Player getPlayer();

    @NotNull
    Location getLocation();

    @NotNull
    Location getHeadLocation();

    int getDimension();

    long getLastDisconnectionTimestamp();

    long getLastConnectionTryTimestamp();

    PlayerId getLastConnectedPlayer();

    PlayerAbilities getAbilities();

    ServiceInventory getInventory();

    @NotNull
    MCServiceCredentials getCredentials();

    @Nullable
    UserAuthentication getAuthentication();

    @Nullable
    UUID getUniqueId();

    @Nullable
    String getName();

    int getEntityId();

    boolean isOnGround();

    ServiceWorldDataProvider getWorldDataProvider();

    @NotNull
    NetworkAddress getServerAddress();

    void chat(@NotNull String message);

    void displayMessage(@NotNull ChatMessageType type, @NotNull String message);

    void chat(@NotNull Component component);

    void displayMessage(@NotNull ChatMessageType type, @NotNull Component component);

    void chat(@NotNull Component... components);

    void displayMessage(@NotNull ChatMessageType type, @NotNull Component... components);

    /* true = client connected and packet sent, false otherwise */
    boolean setTabHeaderAndFooter(Component header, Component footer);

    void resetTabHeaderAndFooter();

    Component getTabHeader();

    Component getTabFooter();

    // IllegalState if this connection is already connected
    @NotNull
    Task<ServiceConnectResult> connect() throws IllegalStateException;

    @Override
    boolean isConnected();

    void unregister();

    Scoreboard getScoreboard();

    BlockAccess getBlockAccess();

    @ApiStatus.Internal
    void syncPackets(Player player, boolean switched);

    @ApiStatus.Internal
    void updateLocation(@NotNull Location location);

    Collection<Player> getViewers();

    void startViewing(Player player);

    void stopViewing(Player player);

    boolean isSneaking();

    boolean isSprinting();

    @Nullable
    Location getTargetBlock(@Nullable Set<Material> transparent, int range);

    @Nullable
    Location getTargetBlock(int range);

    @NotNull
    BlockingObject getTargetObject(int range);

}
