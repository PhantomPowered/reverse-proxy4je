package com.github.derrop.proxy.api.player;

import com.github.derrop.proxy.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

public interface PlayerRepository {

    Collection<ProxiedPlayer> getOnlinePlayers();

    ProxiedPlayer getOnlinePlayer(String name);

    ProxiedPlayer getOnlinePlayer(UUID uniqueId);


    Collection<OfflinePlayer> getOfflinePlayers();

    OfflinePlayer getOfflinePlayer(String name);

    OfflinePlayer getOfflinePlayer(UUID uniqueId);
    
}
