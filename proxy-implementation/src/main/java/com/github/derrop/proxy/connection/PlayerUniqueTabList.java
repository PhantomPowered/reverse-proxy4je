package com.github.derrop.proxy.connection;

import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.tab.TabList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerUniqueTabList extends TabList {
    private ChannelWrapper target;
    private List<UUID> uuids = new ArrayList<>();

    public PlayerUniqueTabList(ChannelWrapper target) {
        this.target = target;
    }

    @Override
    public void onUpdate(PlayerListItem playerListItem) {
        for (PlayerListItem.Item item : playerListItem.getItems()) {
            if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER) {
                this.uuids.add(item.getUuid());
            } else if (playerListItem.getAction() == PlayerListItem.Action.REMOVE_PLAYER) {
                this.uuids.remove(item.getUuid());
            }
        }
    }

    @Override
    public void onPingChange(int ping) {
    }

    @Override
    public void onServerChange() {
        if (this.uuids.isEmpty()) {
            return;
        }
        PlayerListItem playerListItem = new PlayerListItem();
        playerListItem.setAction(PlayerListItem.Action.REMOVE_PLAYER);
        PlayerListItem.Item[] items = new PlayerListItem.Item[this.uuids.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new PlayerListItem.Item();
            items[i].setUuid(this.uuids.get(i));
        }

        this.target.write(playerListItem);
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect() {

    }
}
