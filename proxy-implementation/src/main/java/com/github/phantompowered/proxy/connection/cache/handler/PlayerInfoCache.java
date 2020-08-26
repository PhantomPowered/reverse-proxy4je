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
package com.github.phantompowered.proxy.connection.cache.handler;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.service.playerinfo.PlayerInfoAddEvent;
import com.github.phantompowered.proxy.api.events.connection.service.playerinfo.PlayerInfoRemoveEvent;
import com.github.phantompowered.proxy.api.events.connection.service.playerinfo.PlayerInfoUpdateEvent;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.api.player.GameMode;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.PacketCacheHandler;
import com.github.phantompowered.proxy.connection.player.BasicPlayerInfo;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerPlayerInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerInfoCache implements PacketCacheHandler {

    // TODO if the real player doesn't have the same uuid as the player connected with the server, the player will be displayed twice in the tablist

    private final Collection<PacketPlayServerPlayerInfo.Item> items = new CopyOnWriteArrayList<>();
    private final Collection<PacketPlayServerPlayerInfo.Item> lastRemovedItems = new CopyOnWriteArrayList<>(); // TODO Remove players when despawn packet received?
    private PacketCache packetCache;

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.PLAYER_INFO};
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        this.packetCache = packetCache;
        ServiceConnection connection = packetCache.getTargetProxyClient().getConnection();

        PacketPlayServerPlayerInfo playerListItem = (PacketPlayServerPlayerInfo) newPacket;

        if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
            for (PacketPlayServerPlayerInfo.Item item : this.items) {
                for (PacketPlayServerPlayerInfo.Item item1 : playerListItem.getItems()) {
                    if (item.getUniqueId().equals(item1.getUniqueId())) {
                        this.lastRemovedItems.add(item);
                        this.items.remove(item);
                        connection.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerInfoRemoveEvent(connection, this.toPlayerInfo(item)));
                    }
                }
            }
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.ADD_PLAYER) {
            this.items.addAll(Arrays.asList(playerListItem.getItems()));
            for (PacketPlayServerPlayerInfo.Item item : playerListItem.getItems()) {
                connection.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerInfoAddEvent(connection, this.toPlayerInfo(item)));
            }
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> {
                        item.setDisplayName(newItem.getDisplayName());
                        this.callUpdate(item, connection);
                    }));
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_GAMEMODE) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> {
                        item.setGamemode(newItem.getGamemode());
                        this.callUpdate(item, connection);
                    }));
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_LATENCY) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> {
                        item.setPing(newItem.getPing());
                        this.callUpdate(item, connection);
                    }));
        }
    }

    private void callUpdate(PacketPlayServerPlayerInfo.Item item, ServiceConnection connection) {
        connection.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerInfoUpdateEvent(connection, this.toPlayerInfo(item)));
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (this.packetCache == null) {
            return;
        }

        PacketPlayServerPlayerInfo playerListItem = new PacketPlayServerPlayerInfo();

        playerListItem.setAction(PacketPlayServerPlayerInfo.Action.ADD_PLAYER);
        playerListItem.setItems(this.items.toArray(new PacketPlayServerPlayerInfo.Item[0]));

        if (con instanceof Player) {
            this.replaceOwn((Player) con, playerListItem);
        }

        con.sendPacket(playerListItem);
    }

    @Override
    public void onClientSwitch(Player con) {
        PacketPlayServerPlayerInfo playerListItem = new PacketPlayServerPlayerInfo();

        playerListItem.setAction(PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER);
        playerListItem.setItems(this.items.toArray(new PacketPlayServerPlayerInfo.Item[0]));

        this.replaceOwn(con, playerListItem);

        con.sendPacket(playerListItem);
    }

    private void replaceOwn(Player con, PacketPlayServerPlayerInfo playerListItem) {
        for (int i = 0; i < playerListItem.getItems().length; i++) {
            PacketPlayServerPlayerInfo.Item item = playerListItem.getItems()[i];
            if (item.getUniqueId().equals(this.packetCache.getTargetProxyClient().getAccountUUID())) {
                playerListItem.getItems()[i] = item.clone();
                playerListItem.getItems()[i].setUniqueId(con.getUniqueId());
            }
        }
    }

    public Collection<PacketPlayServerPlayerInfo.Item> getItems() {
        return this.items;
    }

    public boolean isCached(UUID uniqueId) {
        return this.items.stream().anyMatch(item -> item.getUniqueId().equals(uniqueId));
    }

    public PacketPlayServerPlayerInfo.Item getRemovedItem(UUID uniqueId) {
        return this.lastRemovedItems.stream().filter(item -> item.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    public PlayerInfo toPlayerInfo(PacketPlayServerPlayerInfo.Item item) {
        return new BasicPlayerInfo(item.getProfile(), GameMode.getById(item.getGamemode()), item.getPing(), item.getDisplayName());
    }
}
