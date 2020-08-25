package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.launcher.MCProxy;
import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.player.Player;
import com.github.derrop.proxy.api.session.MCServiceCredentials;
import com.github.derrop.proxy.api.network.NetworkAddress;
import com.github.derrop.proxy.connection.reconnect.ReconnectProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DefaultServiceConnector implements ServiceConnector {

    private final MCProxy proxy;

    private final Collection<BasicServiceConnection> onlineClients = new CopyOnWriteArrayList<>();
    private final Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    public DefaultServiceConnector(MCProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public @Nullable BasicServiceConnection findBestConnection(UUID player) {
        if (player != null && this.reconnectProfiles.containsKey(player)) {
            ReconnectProfile profile = this.reconnectProfiles.get(player);
            if (System.currentTimeMillis() < profile.getTimeout()) {
                Optional<BasicServiceConnection> optionalClient = this.onlineClients.stream()
                        .filter(connection -> connection.getPlayer() == null)
                        .filter(connection -> profile.getTargetUniqueId().equals(connection.getUniqueId()))
                        .findFirst();
                if (optionalClient.isPresent()) {
                    this.reconnectProfiles.remove(player);
                    return optionalClient.get();
                }
            }
        }

        return this.onlineClients.stream()
                .filter(proxyClient -> proxyClient.getPlayer() == null)
                .filter(proxyClient -> !this.reconnectProfiles.containsKey(proxyClient.getUniqueId()))
                .findFirst().orElse(null);
    }

    @Override
    public @NotNull ServiceConnection createConnection(MCServiceCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException {
        return new BasicServiceConnection(this.proxy, credentials, serverAddress);
    }

    public void setReconnectTarget(UUID uniqueId, UUID targetUniqueId) {
        this.reconnectProfiles.put(uniqueId, new ReconnectProfile(uniqueId, targetUniqueId));
    }

    @Override
    @NotNull
    public Optional<? extends ServiceConnection> getClientByEmail(String email) {
        return this.onlineClients.stream()
                .filter(connection -> connection.getCredentials().getEmail() != null)
                .filter(connection -> connection.getCredentials().getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<? extends ServiceConnection> getClientByName(String name) {
        return this.onlineClients.stream()
                .filter(connection -> connection.getName() != null && connection.getName().equals(name))
                .findFirst();
    }

    @Override
    @NotNull
    public Collection<BasicServiceConnection> getOnlineClients() {
        return this.onlineClients;
    }

    @Override
    @NotNull
    public Collection<ServiceConnection> getFreeClients() {
        return this.getOnlineClients().stream().filter(proxyClient -> proxyClient.getPlayer() == null).collect(Collectors.toList());
    }

    public Map<UUID, ReconnectProfile> getReconnectProfiles() {
        return this.reconnectProfiles;
    }

    public void addOnlineClient(BasicServiceConnection connection) {
        this.onlineClients.add(connection);
    }

    public void switchClientSafe(Player player, ServiceConnection proxyClient) {
        player.disconnect(TextComponent.of(APIUtil.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + proxyClient.getName()));
        this.setReconnectTarget(player.getUniqueId(), proxyClient.getUniqueId());
    }

    public void unregisterConnection(BasicServiceConnection proxyClient) {
        this.onlineClients.remove(proxyClient);
        proxyClient.close();
    }

    @Override
    public void handleTick() {
        for (BasicServiceConnection onlineClient : this.onlineClients) {
            onlineClient.getClient().handleTick();
        }
    }
}
