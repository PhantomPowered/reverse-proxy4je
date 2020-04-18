/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy;

import com.github.derrop.proxy.account.AccountBiConsumer;
import com.github.derrop.proxy.account.AccountReader;
import com.github.derrop.proxy.api.Configuration;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.database.DatabaseDriver;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.repository.PlayerRepository;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.session.ProvidedSessionService;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.block.DefaultBlockStateRegistry;
import com.github.derrop.proxy.brand.ProxyBrandChangeListener;
import com.github.derrop.proxy.command.DefaultCommandMap;
import com.github.derrop.proxy.command.defaults.*;
import com.github.derrop.proxy.connection.ProxyServer;
import com.github.derrop.proxy.connection.login.ProxyClientLoginHandler;
import com.github.derrop.proxy.connection.reconnect.ReconnectProfile;
import com.github.derrop.proxy.entity.EntityTickHandler;
import com.github.derrop.proxy.entity.player.DefaultPlayerRepository;
import com.github.derrop.proxy.event.DefaultEventManager;
import com.github.derrop.proxy.logging.ILogger;
import com.github.derrop.proxy.network.listener.InitialHandler;
import com.github.derrop.proxy.network.registry.handler.DefaultPacketHandlerRegistry;
import com.github.derrop.proxy.network.registry.packet.DefaultPacketRegistry;
import com.github.derrop.proxy.permission.PermissionProvider;
import com.github.derrop.proxy.plugin.DefaultPluginManager;
import com.github.derrop.proxy.protocol.PacketRegistrar;
import com.github.derrop.proxy.service.BasicServiceRegistry;
import com.github.derrop.proxy.session.BasicProvidedSessionService;
import com.github.derrop.proxy.storage.database.H2DatabaseConfig;
import com.github.derrop.proxy.storage.database.H2DatabaseDriver;
import com.github.derrop.proxy.title.BasicTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.md_5.bungee.connection.ClientPacketHandler;
import net.md_5.bungee.connection.ServerPacketHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MCProxy extends Proxy {

    private static final Path ACCOUNT_PATH = Paths.get("accounts.txt");

    private static MCProxy instance;

    private final ServiceRegistry serviceRegistry = new BasicServiceRegistry();

    private ProxyServer proxyServer = new ProxyServer();
    private PermissionProvider permissionProvider = new PermissionProvider();
    private AccountReader accountReader = new AccountReader();

    private Collection<BasicServiceConnection> onlineClients = new CopyOnWriteArrayList<>();
    private Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    private final ILogger logger;

    protected MCProxy(@NotNull ILogger logger) {
        instance = this;

        this.serviceRegistry.setProvider(null, Proxy.class, this, true);
        this.serviceRegistry.setProvider(null, BlockStateRegistry.class, new DefaultBlockStateRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketHandlerRegistry.class, new DefaultPacketHandlerRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketRegistry.class, new DefaultPacketRegistry(), false, true);
        this.serviceRegistry.setProvider(null, Configuration.class, new JsonConfiguration(), true);
        this.serviceRegistry.setProvider(null, DatabaseDriver.class, new H2DatabaseDriver(), false, true);

        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ProxyClientLoginHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new InitialHandler(this));
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ClientPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ServerPacketHandler());

        this.serviceRegistry.getProviderUnchecked(Configuration.class).load();

        this.logger = logger;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Thread"));
    }

    public void switchClientSafe(Player player, ServiceConnection proxyClient) {
        player.disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + proxyClient.getName()));
        this.setReconnectTarget(player.getUniqueId(), proxyClient.getUniqueId());
    }

    public void unregisterConnection(ServiceConnection proxyClient) {
        this.onlineClients.remove(proxyClient);
    }

    @Override
    public @Nullable BasicServiceConnection findBestConnection(Player player) {
        if (player != null && this.reconnectProfiles.containsKey(player.getUniqueId())) {
            ReconnectProfile profile = this.reconnectProfiles.get(player.getUniqueId());
            if (System.currentTimeMillis() < profile.getTimeout()) {
                Optional<BasicServiceConnection> optionalClient = this.onlineClients.stream()
                        .filter(connection -> connection.getPlayer() == null)
                        .filter(connection -> profile.getTargetUniqueId().equals(connection.getUniqueId()))
                        .findFirst();
                if (optionalClient.isPresent()) {
                    this.reconnectProfiles.remove(player.getUniqueId());
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
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    public void setReconnectTarget(UUID uniqueId, UUID targetUniqueId) {
        this.reconnectProfiles.put(uniqueId, new ReconnectProfile(uniqueId, targetUniqueId));
    }

    public Optional<? extends ServiceConnection> getClientByEmail(String email) {
        return this.onlineClients.stream()
                .filter(connection -> connection.getCredentials().getEmail() != null)
                .filter(connection -> connection.getCredentials().getEmail().equals(email))
                .findFirst();
    }

    public Collection<BasicServiceConnection> getOnlineClients() {
        return this.onlineClients;
    }

    public void addOnlineClient(BasicServiceConnection connection) {
        this.onlineClients.add(connection);
    }

    public Collection<ServiceConnection> getFreeClients() {
        return this.getOnlineClients().stream().filter(proxyClient -> proxyClient.getPlayer() == null).collect(Collectors.toList());
    }

    public void shutdown() {
        if (!Thread.currentThread().getName().equals("Shutdown Thread")) {
            System.exit(0);
        } else {
            this.serviceRegistry.getProviderUnchecked(PluginManager.class).disablePlugins();

            for (ServiceConnection onlineClient : this.getOnlineClients()) {
                if (onlineClient.getPlayer() != null) {
                    onlineClient.getPlayer().disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Shutting down the proxy..."));
                }

                try {
                    onlineClient.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public @NotNull ProvidedTitle createTitle() {
        return new BasicTitle();
    }

    @Override
    public @NotNull ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException {
        return new BasicServiceConnection(this, credentials, serverAddress);
    }

    public PermissionProvider getPermissionProvider() {
        return permissionProvider;
    }

    public Map<UUID, ReconnectProfile> getReconnectProfiles() {
        return reconnectProfiles;
    }

    public ILogger getLogger() {
        return logger;
    }

    public static MCProxy getInstance() {
        return instance;
    }

    public void bootstrap(int port) throws IOException {
        PacketRegistrar.registerPackets();

        this.proxyServer.start(new InetSocketAddress(port));

        this.serviceRegistry.setProvider(null, ProvidedSessionService.class, new BasicProvidedSessionService(), false, true);
        this.serviceRegistry.setProvider(null, EventManager.class, new DefaultEventManager(), false, true);
        this.serviceRegistry.setProvider(null, PluginManager.class, new DefaultPluginManager(Paths.get("plugins")), false, true);

        this.serviceRegistry.getProviderUnchecked(DatabaseDriver.class).connect(new H2DatabaseConfig());

        this.serviceRegistry.setProvider(null, PlayerRepository.class, new DefaultPlayerRepository(this), true);

        this.serviceRegistry.getProviderUnchecked(PluginManager.class).detectPlugins();
        this.serviceRegistry.getProviderUnchecked(PluginManager.class).loadPlugins();

        this.handleCommands();

        this.serviceRegistry.getProviderUnchecked(EventManager.class).registerListener(new ProxyBrandChangeListener());
        if (Files.notExists(ACCOUNT_PATH)) {
            this.accountReader.writeDefaults(ACCOUNT_PATH);
        }

        this.accountReader.readAccounts(ACCOUNT_PATH, new AccountBiConsumer());
        EntityTickHandler.startTick();

        this.serviceRegistry.getProviderUnchecked(PluginManager.class).enablePlugins();
    }

    private void handleCommands() {
        CommandMap commandMap = new DefaultCommandMap();

        commandMap.registerCommand(null, new CommandAccount(), "acc", "account");
        commandMap.registerCommand(null, new CommandAlert(), "alert");
        commandMap.registerCommand(null, new CommandChat(), "chat");
        commandMap.registerCommand(null, new CommandConnect(), "connect");
        commandMap.registerCommand(null, new CommandForEach(), "foreach");
        commandMap.registerCommand(null, new CommandHelp(commandMap), "help", "ask", "?");
        commandMap.registerCommand(null, new CommandInfo(), "info", "information", "i");
        commandMap.registerCommand(null, new CommandKick(), "kick");
        commandMap.registerCommand(null, new CommandList(), "list", "glist");
        commandMap.registerCommand(null, new CommandSwitch(), "switch");

        commandMap.registerCommand(null, new CommandAdf(), "adf");

        this.serviceRegistry.setProvider(null, CommandMap.class, commandMap, false, true);
    }

    // todo we could put information like "CPS (autoclicker), PlayerESP (is this possible in the 1.8?)" into the action bar
    //  Or maybe an extra program (like a labymod addon) or a standalone program which can be opened on a second screen (or the mobile?) to display some information
    //  Or maybe just a website for that?
    //  And add bac click limit disable

}
