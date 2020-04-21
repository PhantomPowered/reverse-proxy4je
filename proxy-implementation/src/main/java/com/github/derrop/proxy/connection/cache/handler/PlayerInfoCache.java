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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerInfoCache implements PacketCacheHandler {
    @Override
    public int[] getPacketIDs() {
        return new int[]{56};
    }

    private Collection<PacketPlayServerPlayerInfo.Item> items = new CopyOnWriteArrayList<>();

    private Collection<PacketPlayServerPlayerInfo.Item> lastRemovedItems = new ArrayList<>();

    private PacketCache packetCache;

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.packetCache = packetCache;

        PacketPlayServerPlayerInfo playerListItem = (PacketPlayServerPlayerInfo) newPacket.getDeserializedPacket();

        if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
            for (PacketPlayServerPlayerInfo.Item item : this.items) {
                for (PacketPlayServerPlayerInfo.Item item1 : playerListItem.getItems()) {
                    if (item.getUniqueId().equals(item1.getUniqueId())) {
                        this.lastRemovedItems.add(item);
                        this.items.remove(item);
                    }
                }
            }
        }

        if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.ADD_PLAYER) {
            this.items.addAll(Arrays.asList(playerListItem.getItems()));
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> item.setDisplayName(newItem.getDisplayName())));
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_GAMEMODE) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> item.setGamemode(newItem.getGamemode())));
        } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.UPDATE_LATENCY) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUniqueId().equals(item.getUniqueId())).findFirst()
                    .ifPresent(newItem -> item.setPing(newItem.getPing())));
        }
    }

    @Override
    public void sendCached(PacketSender con) {
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

}
