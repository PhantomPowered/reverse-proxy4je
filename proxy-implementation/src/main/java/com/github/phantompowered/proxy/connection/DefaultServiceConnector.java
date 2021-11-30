package com.github.phantompowered.proxy.connection;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.connection.reconnect.ReconnectProfile;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.Component;
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

    private final ServiceRegistry serviceRegistry;
    private final Collection<BasicServiceConnection> onlineClients = new CopyOnWriteArrayList<>();
    private final Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    public DefaultServiceConnector(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public boolean hasReconnectProfile(UUID playerUniqueId) {
        return this.reconnectProfiles.containsKey(playerUniqueId);
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
        BasicServiceConnection connection = new BasicServiceConnection(this.serviceRegistry, credentials, serverAddress, ProtocolIds.Versions.MINECRAFT_1_8);
        this.onlineClients.add(connection);
        return connection;
    }

    @Override
    public ServiceConnection createConnection(MCServiceCredentials credentials, UserAuthentication authentication, NetworkAddress serverAddress) throws AuthenticationException {
        BasicServiceConnection connection = new BasicServiceConnection(this.serviceRegistry, credentials, authentication, serverAddress, ProtocolIds.Versions.MINECRAFT_1_8);
        this.onlineClients.add(connection);
        return connection;
    }

    public void setReconnectTarget(UUID uniqueId, UUID targetUniqueId) {
        this.reconnectProfiles.put(uniqueId, new ReconnectProfile(uniqueId, targetUniqueId));
    }

    @Override
    @NotNull
    public Optional<ServiceConnection> getClientByEmail(String email) {
        return this.onlineClients.stream()
                .filter(connection -> connection.getCredentials().getEmail() != null)
                .filter(connection -> connection.getCredentials().getEmail().equals(email))
                .map(connection -> (ServiceConnection) connection)
                .findFirst();
    }

    @Override
    public Optional<ServiceConnection> getClientByName(String name) {
        return this.onlineClients.stream()
                .filter(connection -> connection.getName() != null && connection.getName().equals(name))
                .map(connection -> (ServiceConnection) connection)
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

    public void switchClientSafe(Player player, ServiceConnection proxyClient) {
        player.disconnect(Component.text(APIUtil.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + proxyClient.getName()));
        this.setReconnectTarget(player.getUniqueId(), proxyClient.getUniqueId());
    }

    @Override
    public void handleTick() {
        for (BasicServiceConnection onlineClient : this.onlineClients) {
            if (onlineClient.getClient() == null) {
                continue;
            }

            onlineClient.getClient().handleTick();
        }
    }
}
