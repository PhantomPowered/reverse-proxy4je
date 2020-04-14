package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.protocol.packet.PlayerListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PlayerInfoCache implements PacketCacheHandler {
    @Override
    public int[] getPacketIDs() {
        return new int[]{56};
    }

    private Collection<PlayerListItem.Item> items = new ArrayList<>();

    private PacketCache packetCache;

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.packetCache = packetCache;

        PlayerListItem playerListItem = (PlayerListItem) newPacket.getDeserializedPacket();

        if (playerListItem.getAction() == PlayerListItem.Action.REMOVE_PLAYER) {
            this.items.removeIf(item -> Arrays.stream(playerListItem.getItems()).anyMatch(item1 -> item1.getUuid().equals(item.getUuid())));
        }

        if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER) {
            this.items.addAll(Arrays.asList(playerListItem.getItems()));
        } else if (playerListItem.getAction() == PlayerListItem.Action.UPDATE_DISPLAY_NAME) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUuid().equals(item.getUuid())).findFirst()
                    .ifPresent(newItem -> item.setDisplayName(newItem.getDisplayName())));
        } else if (playerListItem.getAction() == PlayerListItem.Action.UPDATE_GAMEMODE) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUuid().equals(item.getUuid())).findFirst()
                    .ifPresent(newItem -> item.setGamemode(newItem.getGamemode())));
        } else if (playerListItem.getAction() == PlayerListItem.Action.UPDATE_LATENCY) {
            this.items.forEach(item -> Arrays.stream(playerListItem.getItems()).filter(item1 -> item1.getUuid().equals(item.getUuid())).findFirst()
                    .ifPresent(newItem -> item.setPing(newItem.getPing())));
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.packetCache == null) {
            return;
        }

        PlayerListItem playerListItem = new PlayerListItem();

        playerListItem.setAction(PlayerListItem.Action.ADD_PLAYER);
        playerListItem.setItems(this.items.toArray(new PlayerListItem.Item[0]));

        if (con instanceof Player) {
            this.replaceOwn((Player) con, playerListItem);
        }

        con.sendPacket(playerListItem);
    }

    @Override
    public void onClientSwitch(Player con) {
        PlayerListItem playerListItem = new PlayerListItem();

        playerListItem.setAction(PlayerListItem.Action.REMOVE_PLAYER);
        playerListItem.setItems(this.items.toArray(new PlayerListItem.Item[0]));

        this.replaceOwn(con, playerListItem);

        con.sendPacket(playerListItem);
    }

    private void replaceOwn(Player con, PlayerListItem playerListItem) {
        for (int i = 0; i < playerListItem.getItems().length; i++) {
            PlayerListItem.Item item = playerListItem.getItems()[i];
            if (item.getUuid().equals(this.packetCache.getTargetProxyClient().getAccountUUID())) {
                playerListItem.getItems()[i] = item.clone();
                playerListItem.getItems()[i].setUuid(con.getUniqueId());
            }
        }
    }

}
