package com.github.derrop.proxy.api;

import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.player.PlayerRepository;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Proxy {

    @NotNull
    public abstract EventManager getEventManager();

    @NotNull
    public abstract PlayerRepository getPlayerRepository();

    @NotNull
    public abstract PluginManager getPluginManager();

    @NotNull
    public abstract ProvidedTitle createTitle();

    @NotNull
    public abstract ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException;

    @Nullable
    public abstract ServiceConnection findBestConnection(ProxiedPlayer player);

    @NotNull
    public abstract ServiceRegistry getServiceRegistry();

    public abstract void shutdown();

}
