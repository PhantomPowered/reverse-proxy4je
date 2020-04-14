package com.github.derrop.proxy.api;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.player.PlayerRepository;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Proxy {

    @NotNull
    public abstract PlayerRepository getPlayerRepository();

    @NotNull
    public abstract ProvidedTitle createTitle();

    @NotNull
    public abstract ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException;

    @Nullable
    public abstract ServiceConnection findBestConnection(Player player);

    @NotNull
    public abstract ServiceRegistry getServiceRegistry();

    public abstract void shutdown();

}
