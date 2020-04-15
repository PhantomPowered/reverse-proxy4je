package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import net.md_5.bungee.tab.TabList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerUniqueTabList extends TabList {
    private NetworkChannel target;
    private List<UUID> uuids = new ArrayList<>();

    public PlayerUniqueTabList(NetworkChannel target) {
        this.target = target;
    }

    @Override
    public void onUpdate(PacketPlayServerPlayerInfo playerListItem) {
        for (PacketPlayServerPlayerInfo.Item item : playerListItem.getItems()) {
            if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.ADD_PLAYER) {
                this.uuids.add(item.getUuid());
            } else if (playerListItem.getAction() == PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
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
        PacketPlayServerPlayerInfo playerListItem = new PacketPlayServerPlayerInfo();
        playerListItem.setAction(PacketPlayServerPlayerInfo.Action.REMOVE_PLAYER);
        PacketPlayServerPlayerInfo.Item[] items = new PacketPlayServerPlayerInfo.Item[this.uuids.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new PacketPlayServerPlayerInfo.Item();
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
