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
package com.github.phantompowered.proxy.launcher;

import com.github.phantompowered.proxy.DefaultImplementationMapper;
import com.github.phantompowered.proxy.account.BasicProvidedSessionService;
import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.ImplementationMapper;
import com.github.phantompowered.proxy.api.block.BlockStateRegistry;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.configuration.Configuration;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.connection.Whitelist;
import com.github.phantompowered.proxy.api.database.DatabaseDriver;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistry;
import com.github.phantompowered.proxy.api.paste.PasteServerProvider;
import com.github.phantompowered.proxy.api.ping.ServerPingProvider;
import com.github.phantompowered.proxy.api.player.PlayerRepository;
import com.github.phantompowered.proxy.api.player.id.PlayerIdStorage;
import com.github.phantompowered.proxy.api.plugin.PluginManager;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.session.ProvidedSessionService;
import com.github.phantompowered.proxy.api.tick.TickHandlerProvider;
import com.github.phantompowered.proxy.block.DefaultBlockStateRegistry;
import com.github.phantompowered.proxy.command.DefaultCommandMap;
import com.github.phantompowered.proxy.command.defaults.CommandAccount;
import com.github.phantompowered.proxy.command.defaults.CommandAlert;
import com.github.phantompowered.proxy.command.defaults.CommandChat;
import com.github.phantompowered.proxy.command.defaults.CommandClear;
import com.github.phantompowered.proxy.command.defaults.CommandConnect;
import com.github.phantompowered.proxy.command.defaults.CommandDebug;
import com.github.phantompowered.proxy.command.defaults.CommandFind;
import com.github.phantompowered.proxy.command.defaults.CommandForEach;
import com.github.phantompowered.proxy.command.defaults.CommandHelp;
import com.github.phantompowered.proxy.command.defaults.CommandInfo;
import com.github.phantompowered.proxy.command.defaults.CommandKick;
import com.github.phantompowered.proxy.command.defaults.CommandList;
import com.github.phantompowered.proxy.command.defaults.CommandPermissions;
import com.github.phantompowered.proxy.command.defaults.CommandPing;
import com.github.phantompowered.proxy.command.defaults.CommandPlugins;
import com.github.phantompowered.proxy.command.defaults.CommandReplace;
import com.github.phantompowered.proxy.command.defaults.CommandStop;
import com.github.phantompowered.proxy.command.defaults.CommandSwitch;
import com.github.phantompowered.proxy.command.defaults.CommandWhitelist;
import com.github.phantompowered.proxy.configuration.JsonConfiguration;
import com.github.phantompowered.proxy.connection.DefaultServiceConnector;
import com.github.phantompowered.proxy.connection.ProxyServer;
import com.github.phantompowered.proxy.connection.handler.ClientPacketHandler;
import com.github.phantompowered.proxy.connection.handler.PingPacketHandler;
import com.github.phantompowered.proxy.connection.handler.ServerPacketHandler;
import com.github.phantompowered.proxy.connection.login.ProxyClientLoginHandler;
import com.github.phantompowered.proxy.connection.player.DefaultPlayerRepository;
import com.github.phantompowered.proxy.connection.reconnect.ServiceReconnectionHandler;
import com.github.phantompowered.proxy.connection.whitelist.DefaultWhitelist;
import com.github.phantompowered.proxy.entity.EntityTickHandler;
import com.github.phantompowered.proxy.event.DefaultEventManager;
import com.github.phantompowered.proxy.network.SimpleChannelInitializer;
import com.github.phantompowered.proxy.network.listener.InitialHandler;
import com.github.phantompowered.proxy.network.registry.handler.DefaultPacketHandlerRegistry;
import com.github.phantompowered.proxy.network.registry.packet.DefaultPacketRegistry;
import com.github.phantompowered.proxy.paste.DefaultPasteServerProvider;
import com.github.phantompowered.proxy.ping.DefaultServerPingProvider;
import com.github.phantompowered.proxy.plugin.DefaultPluginManager;
import com.github.phantompowered.proxy.protocol.PacketRegistrar;
import com.github.phantompowered.proxy.storage.DefaultPlayerIdStorage;
import com.github.phantompowered.proxy.storage.MCServiceCredentialsStorage;
import com.github.phantompowered.proxy.storage.database.H2DatabaseConfig;
import com.github.phantompowered.proxy.storage.database.H2DatabaseDriver;
import com.github.phantompowered.proxy.tick.DefaultTickHandlerProvider;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PhantomProxy {

    private final ServiceRegistry serviceRegistry;
    private final ProxyServer proxyServer;
    private final SimpleChannelInitializer baseChannelInitializer;

    protected PhantomProxy(ServiceRegistry registry) {
        this.serviceRegistry = registry;
        this.proxyServer = new ProxyServer(this.serviceRegistry);
        this.baseChannelInitializer = new SimpleChannelInitializer(this.serviceRegistry);

        System.out.println("Registering default services...");
        this.serviceRegistry.setProvider(null, SimpleChannelInitializer.class, this.baseChannelInitializer, false, true);
        this.serviceRegistry.setProvider(null, PasteServerProvider.class, new DefaultPasteServerProvider(), false, true);
        this.serviceRegistry.setProvider(null, BlockStateRegistry.class, new DefaultBlockStateRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketHandlerRegistry.class, new DefaultPacketHandlerRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketRegistry.class, new DefaultPacketRegistry(), false, true);
        this.serviceRegistry.setProvider(null, Configuration.class, new JsonConfiguration(), true);
        this.serviceRegistry.setProvider(null, DatabaseDriver.class, new H2DatabaseDriver(), false, true);
        this.serviceRegistry.setProvider(null, ServiceConnector.class, new DefaultServiceConnector(this.serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, ServerPingProvider.class, new DefaultServerPingProvider(this.serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, TickHandlerProvider.class, new DefaultTickHandlerProvider(), true);
        this.serviceRegistry.setProvider(null, ImplementationMapper.class, new DefaultImplementationMapper(), true);

        System.out.println("Registering packet handlers...");
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new PingPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ProxyClientLoginHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new InitialHandler(this.serviceRegistry));
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ClientPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ServerPacketHandler());

        System.out.println("Loading configuration...");
        this.serviceRegistry.getProviderUnchecked(Configuration.class).load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Thread"));
    }

    protected @NotNull ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    private void shutdown() {
        if (!Thread.currentThread().getName().equals("Shutdown Thread")) {
            System.exit(0);
        } else {
            this.serviceRegistry.getProviderUnchecked(PluginManager.class).disablePlugins();

            for (ServiceConnection onlineClient : this.serviceRegistry.getProviderUnchecked(ServiceConnector.class).getOnlineClients()) {
                if (onlineClient.getPlayer() != null) {
                    onlineClient.getPlayer().disconnect(Component.text(APIUtil.MESSAGE_PREFIX + "Shutting down the proxy..."));
                }

                try {
                    onlineClient.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            this.serviceRegistry.getProviderUnchecked(DatabaseDriver.class).close();

            this.proxyServer.close();
        }
    }

    public SimpleChannelInitializer getBaseChannelInitializer() {
        return this.baseChannelInitializer;
    }

    public void bootstrap(int port, long start) {
        System.out.println("Registering incoming packets...");
        PacketRegistrar.registerPackets(this.serviceRegistry.getProviderUnchecked(PacketRegistry.class));

        System.out.println("Connecting to database...");
        this.serviceRegistry.getProviderUnchecked(DatabaseDriver.class).connect(new H2DatabaseConfig());
        this.serviceRegistry.setProvider(null, ProvidedSessionService.class, new BasicProvidedSessionService(this.serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, EventManager.class, new DefaultEventManager(serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, PluginManager.class, new DefaultPluginManager(Paths.get("plugins"), this.serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, MCServiceCredentialsStorage.class, new MCServiceCredentialsStorage(this.serviceRegistry));
        this.serviceRegistry.setProvider(null, PlayerRepository.class, new DefaultPlayerRepository(this.serviceRegistry), true);
        this.serviceRegistry.setProvider(null, PlayerIdStorage.class, new DefaultPlayerIdStorage(this.serviceRegistry), false, true);
        this.serviceRegistry.setProvider(null, Whitelist.class, new DefaultWhitelist(this.serviceRegistry), false);

        System.out.println("Loading plugins...");
        this.serviceRegistry.getProviderUnchecked(PluginManager.class).detectPlugins();
        this.serviceRegistry.getProviderUnchecked(PluginManager.class).loadPlugins();

        System.out.println("Loading commands...");
        this.handleCommands();

        if (!Boolean.getBoolean("proxy.do-not-read.accounts")) {
            System.out.println("Reading accounts...");
            this.readAccounts();
        } else {
            System.out.println("Reading of accounts is disabled");
        }

        System.out.println("Starting entity tick...");
        EntityTickHandler.startTick(this.serviceRegistry);

        System.out.println("Enabling plugins...");
        this.serviceRegistry.getProviderUnchecked(PluginManager.class).enablePlugins();

        System.out.println("Starting proxy listener on " + port + "...");
        this.proxyServer.start(new InetSocketAddress(port));

        this.serviceRegistry.getProviderUnchecked(TickHandlerProvider.class).registerHandler(this.serviceRegistry.getProviderUnchecked(ServiceConnector.class));

        System.out.println("Starting main loop...");
        ((DefaultTickHandlerProvider) this.serviceRegistry.getProviderUnchecked(TickHandlerProvider.class)).startMainLoop();

        double bootTime = (System.currentTimeMillis() - start) / 1000d;
        System.out.println("Done! (" + new DecimalFormat("##.###").format(bootTime) + "s)");
    }

    private void readAccounts() {
        APIUtil.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new ServiceReconnectionHandler(this.serviceRegistry), 10, 10, TimeUnit.SECONDS);

        ServiceConnector connector = this.serviceRegistry.getProviderUnchecked(ServiceConnector.class);
        MCServiceCredentialsStorage storage = this.serviceRegistry.getProviderUnchecked(MCServiceCredentialsStorage.class);
        for (MCServiceCredentials credentials : storage.getAll()) {
            NetworkAddress address = NetworkAddress.parse(credentials.getDefaultServer());
            if (address == null) {
                continue;
            }

            try {
                connector.createConnection(credentials, address).connect().get(10, TimeUnit.SECONDS);

                Thread.sleep(500);
            } catch (AuthenticationException | InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCommands() {
        CommandMap commandMap = new DefaultCommandMap();

        commandMap.registerCommand(null, new CommandPing(this.serviceRegistry), "ping");
        commandMap.registerCommand(null, new CommandAccount(this.serviceRegistry), "acc", "account");
        commandMap.registerCommand(null, new CommandAlert(this.serviceRegistry), "alert");
        commandMap.registerCommand(null, new CommandChat(this.serviceRegistry), "chat");
        commandMap.registerCommand(null, new CommandConnect(this.serviceRegistry), "connect");
        commandMap.registerCommand(null, new CommandForEach(this.serviceRegistry), "foreach");
        commandMap.registerCommand(null, new CommandHelp(commandMap), "help", "ask", "?");
        commandMap.registerCommand(null, new CommandInfo(this.serviceRegistry), "info", "information", "i");
        commandMap.registerCommand(null, new CommandKick(this.serviceRegistry), "kick");
        commandMap.registerCommand(null, new CommandList(this.serviceRegistry), "list", "glist");
        commandMap.registerCommand(null, new CommandPlugins(this.serviceRegistry), "plugins", "pl");
        commandMap.registerCommand(null, new CommandSwitch(this.serviceRegistry), "switch");
        commandMap.registerCommand(null, new CommandStop(), "stop", "end", "shutdown", "bye");
        commandMap.registerCommand(null, new CommandPermissions(this.serviceRegistry), "perms");
        commandMap.registerCommand(null, new CommandFind(), "find");
        commandMap.registerCommand(null, new CommandReplace(), "replace");
        commandMap.registerCommand(null, new CommandDebug(this.serviceRegistry), "debug");
        commandMap.registerCommand(null, new CommandWhitelist(this.serviceRegistry), "whitelist");
        commandMap.registerCommand(null, new CommandClear(this.serviceRegistry), "clear", "cls");

        this.serviceRegistry.setProvider(null, CommandMap.class, commandMap, false, true);
    }
}
