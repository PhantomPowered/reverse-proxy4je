package de.derrop.minecraft.proxy.player;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.player.OfflinePlayer;
import de.derrop.minecraft.proxy.api.player.PlayerRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultPlayerRepository implements PlayerRepository {

    private final MCProxy proxy;

    public DefaultPlayerRepository(MCProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Collection<ProxiedPlayer> getOnlinePlayers() {
        return this.proxy.getOnlineClients().stream().map(ServiceConnection::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public ProxiedPlayer getOnlinePlayer(String name) {
        return this.proxy.getOnlineClients().stream().map(ServiceConnection::getPlayer).filter(Objects::nonNull).filter(connection -> connection.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public ProxiedPlayer getOnlinePlayer(UUID uniqueId) {
        return this.proxy.getOnlineClients().stream().map(ServiceConnection::getPlayer).filter(Objects::nonNull).filter(connection -> connection.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    // todo
    @Override
    public Collection<OfflinePlayer> getOfflinePlayers() {
        return null;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        return null;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        return null;
    }
}
