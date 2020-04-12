package de.derrop.minecraft.proxy.api;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.derrop.minecraft.proxy.api.command.CommandMap;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.event.EventManager;
import de.derrop.minecraft.proxy.api.player.PlayerRepository;
import de.derrop.minecraft.proxy.api.plugin.PluginManager;
import de.derrop.minecraft.proxy.api.util.MCCredentials;
import de.derrop.minecraft.proxy.api.util.NetworkAddress;
import de.derrop.minecraft.proxy.api.util.Title;
import org.jetbrains.annotations.NotNull;

public abstract class Proxy {

    @NotNull
    public abstract EventManager getEventManager();

    @NotNull
    public abstract CommandMap getCommandMap();

    @NotNull
    public abstract PlayerRepository getPlayerRepository();

    @NotNull
    public abstract PluginManager getPluginManager();

    public abstract Title createTitle();

    public abstract ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException;

    public abstract ServiceConnection findBestConnection(ProxiedPlayer player);


    public abstract void shutdown();

}
