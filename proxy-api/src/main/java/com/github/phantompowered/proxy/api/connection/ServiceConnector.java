package com.github.phantompowered.proxy.api.connection;

import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.tick.TickHandler;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ServiceConnector extends TickHandler {

    @NotNull
    ServiceConnection createConnection(MCServiceCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException;

    @Nullable
    ServiceConnection findBestConnection(UUID playerUniqueId);

    @NotNull
    Optional<ServiceConnection> getClientByEmail(String email);

    Optional<ServiceConnection> getClientByName(String name);

    @NotNull
    Collection<? extends ServiceConnection> getOnlineClients();

    @NotNull
    Collection<? extends ServiceConnection> getFreeClients();

}
