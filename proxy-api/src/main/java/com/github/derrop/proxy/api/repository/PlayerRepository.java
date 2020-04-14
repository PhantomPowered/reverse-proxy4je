package com.github.derrop.proxy.api.repository;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.entity.player.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;

public interface PlayerRepository {

    Collection<Player> getOnlinePlayers();

    Player getOnlinePlayer(String name);

    Player getOnlinePlayer(UUID uniqueId);


    Collection<OfflinePlayer> getOfflinePlayers();

    OfflinePlayer getOfflinePlayer(String name);

    OfflinePlayer getOfflinePlayer(UUID uniqueId);
    
}
