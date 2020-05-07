package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface ServiceConnector {

    @NotNull
    ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException;

    @Nullable
    ServiceConnection findBestConnection(Player player);

    @NotNull
    Optional<? extends ServiceConnection> getClientByEmail(String email);

    @NotNull
    Collection<? extends ServiceConnection> getOnlineClients();

    @NotNull
    Collection<? extends ServiceConnection> getFreeClients();

}
