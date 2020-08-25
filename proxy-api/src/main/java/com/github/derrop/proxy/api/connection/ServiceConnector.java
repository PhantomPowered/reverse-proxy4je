package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.tick.TickHandler;
import com.github.derrop.proxy.api.session.MCServiceCredentials;
import com.github.derrop.proxy.api.network.NetworkAddress;
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
    Optional<? extends ServiceConnection> getClientByEmail(String email);

    Optional<? extends ServiceConnection> getClientByName(String name);

    @NotNull
    Collection<? extends ServiceConnection> getOnlineClients();

    @NotNull
    Collection<? extends ServiceConnection> getFreeClients();

}
