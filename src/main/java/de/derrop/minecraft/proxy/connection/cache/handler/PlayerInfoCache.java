package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.connection.UserConnection;
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
    public void sendCached(UserConnection con) {
        if (this.packetCache == null) {
            return;
        }

        PlayerListItem playerListItem = new PlayerListItem();

        playerListItem.setAction(PlayerListItem.Action.ADD_PLAYER);
        playerListItem.setItems(this.items.toArray(new PlayerListItem.Item[0]));

        this.replaceOwn(con, playerListItem);

        con.unsafe().sendPacket(playerListItem);
    }

    @Override
    public void onClientSwitch(UserConnection con) {
        PlayerListItem playerListItem = new PlayerListItem();

        playerListItem.setAction(PlayerListItem.Action.REMOVE_PLAYER);
        playerListItem.setItems(this.items.toArray(new PlayerListItem.Item[0]));

        this.replaceOwn(con, playerListItem);

        con.unsafe().sendPacket(playerListItem);
    }

    private void replaceOwn(UserConnection con, PlayerListItem playerListItem) {
        for (int i = 0; i < playerListItem.getItems().length; i++) {
            PlayerListItem.Item item = playerListItem.getItems()[i];
            if (item.getUuid().equals(this.packetCache.getTargetProxyClient().getAccountUUID())) {
                playerListItem.getItems()[i] = item.clone();
                playerListItem.getItems()[i].setUuid(con.getUniqueId());
            }
        }
    }

}
