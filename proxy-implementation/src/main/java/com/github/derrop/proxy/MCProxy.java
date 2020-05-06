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
import com.github.derrop.proxy.account.BasicProvidedSessionService;
import com.github.derrop.proxy.api.Configuration;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.database.DatabaseDriver;
import com.github.derrop.proxy.api.entity.player.PlayerRepository;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.api.network.registry.packet.PacketRegistry;
import com.github.derrop.proxy.api.ping.ServerPingProvider;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.session.ProvidedSessionService;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.block.DefaultBlockStateRegistry;
import com.github.derrop.proxy.brand.ProxyBrandChangeListener;
import com.github.derrop.proxy.command.DefaultCommandMap;
import com.github.derrop.proxy.command.defaults.*;
import com.github.derrop.proxy.config.JsonConfiguration;
import com.github.derrop.proxy.connection.DefaultServiceConnector;
import com.github.derrop.proxy.connection.ProxyServer;
import com.github.derrop.proxy.connection.handler.NameUUIDRewritePacketHandler;
import com.github.derrop.proxy.connection.handler.ClientPacketHandler;
import com.github.derrop.proxy.connection.handler.PingPacketHandler;
import com.github.derrop.proxy.connection.handler.ServerPacketHandler;
import com.github.derrop.proxy.connection.login.ProxyClientLoginHandler;
import com.github.derrop.proxy.entity.EntityTickHandler;
import com.github.derrop.proxy.entity.player.DefaultPlayerRepository;
import com.github.derrop.proxy.event.DefaultEventManager;
import com.github.derrop.proxy.network.SimpleChannelInitializer;
import com.github.derrop.proxy.network.listener.InitialHandler;
import com.github.derrop.proxy.network.registry.handler.DefaultPacketHandlerRegistry;
import com.github.derrop.proxy.network.registry.packet.DefaultPacketRegistry;
import com.github.derrop.proxy.ping.DefaultServerPingProvider;
import com.github.derrop.proxy.plugin.DefaultPluginManager;
import com.github.derrop.proxy.protocol.PacketRegistrar;
import com.github.derrop.proxy.service.BasicServiceRegistry;
import com.github.derrop.proxy.storage.database.H2DatabaseConfig;
import com.github.derrop.proxy.storage.database.H2DatabaseDriver;
import com.github.derrop.proxy.title.BasicTitle;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MCProxy extends Proxy {

    private static final Path ACCOUNT_PATH = Paths.get("accounts.txt");

    private final ServiceRegistry serviceRegistry = new BasicServiceRegistry();

    private final ProxyServer proxyServer = new ProxyServer(this);
    private final AccountReader accountReader = new AccountReader();

    private final SimpleChannelInitializer baseChannelInitializer = new SimpleChannelInitializer(this.serviceRegistry);

    protected MCProxy() {
        this.serviceRegistry.setProvider(null, Proxy.class, this, true);
        this.serviceRegistry.setProvider(null, BlockStateRegistry.class, new DefaultBlockStateRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketHandlerRegistry.class, new DefaultPacketHandlerRegistry(), false, true);
        this.serviceRegistry.setProvider(null, PacketRegistry.class, new DefaultPacketRegistry(), false, true);
        this.serviceRegistry.setProvider(null, Configuration.class, new JsonConfiguration(), true);
        this.serviceRegistry.setProvider(null, DatabaseDriver.class, new H2DatabaseDriver(), false, true);
        this.serviceRegistry.setProvider(null, ServiceConnector.class, new DefaultServiceConnector(this), false, true);

        this.serviceRegistry.setProvider(null, ServerPingProvider.class, new DefaultServerPingProvider(this), false, true);

        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new PingPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ProxyClientLoginHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new InitialHandler(this));
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ClientPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new ServerPacketHandler());
        this.serviceRegistry.getProviderUnchecked(PacketHandlerRegistry.class).registerPacketHandlerClass(null, new NameUUIDRewritePacketHandler());

        this.serviceRegistry.getProviderUnchecked(Configuration.class).load();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Thread"));
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public void shutdown() {
        if (!Thread.currentThread().getName().equals("Shutdown Thread")) {
            System.exit(0);
        } else {
            this.serviceRegistry.getProviderUnchecked(PluginManager.class).disablePlugins();

            for (ServiceConnection onlineClient : this.serviceRegistry.getProviderUnchecked(ServiceConnector.class).getOnlineClients()) {
                if (onlineClient.getPlayer() != null) {
                    onlineClient.getPlayer().disconnect(TextComponent.of(Constants.MESSAGE_PREFIX + "Shutting down the proxy..."));
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

    public SimpleChannelInitializer getBaseChannelInitializer() {
        return this.baseChannelInitializer;
    }

    public void bootstrap(int port) throws IOException {
        PacketRegistrar.registerPackets(this.serviceRegistry.getProviderUnchecked(PacketRegistry.class));

        /*while (!Thread.interrupted()) {
            ServerPing ping = this.serviceRegistry.getProviderUnchecked(ServerPingProvider.class).pingServer(NetworkAddress.parse("91.218.67.147")).getUninterruptedly(5, TimeUnit.SECONDS);
            if (ping == null) {
                System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + ": ERROR");
            } else {
                System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + ": " + ping.getPlayers().getOnline() + "/" + ping.getPlayers().getMax());
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        if (true) {
            return;u
        }*/

        this.serviceRegistry.setProvider(null, ProvidedSessionService.class, new BasicProvidedSessionService(), false, true);
        this.serviceRegistry.setProvider(null, EventManager.class, new DefaultEventManager(), false, true);
        this.serviceRegistry.setProvider(null, PluginManager.class, new DefaultPluginManager(Paths.get("plugins"), this.serviceRegistry), false, true);

        this.serviceRegistry.getProviderUnchecked(DatabaseDriver.class).connect(new H2DatabaseConfig());

        this.serviceRegistry.setProvider(null, PlayerRepository.class, new DefaultPlayerRepository(this.serviceRegistry), true);

        this.serviceRegistry.getProviderUnchecked(PluginManager.class).detectPlugins();
        this.serviceRegistry.getProviderUnchecked(PluginManager.class).loadPlugins();

        this.handleCommands();

        this.serviceRegistry.getProviderUnchecked(EventManager.class).registerListener(null, new ProxyBrandChangeListener());
        if (Files.notExists(ACCOUNT_PATH)) {
            this.accountReader.writeDefaults(ACCOUNT_PATH);
        }

        this.accountReader.readAccounts(ACCOUNT_PATH, new AccountBiConsumer(this));
        EntityTickHandler.startTick(this.serviceRegistry);

        this.serviceRegistry.getProviderUnchecked(PluginManager.class).enablePlugins();

        this.proxyServer.start(new InetSocketAddress(port));
    }

    private void handleCommands() {
        CommandMap commandMap = new DefaultCommandMap();

        commandMap.registerCommand(null, new CommandAccount(this.serviceRegistry), "acc", "account");
        commandMap.registerCommand(null, new CommandAlert(this.serviceRegistry), "alert");
        commandMap.registerCommand(null, new CommandChat(this.serviceRegistry), "chat");
        commandMap.registerCommand(null, new CommandConnect(this.serviceRegistry), "connect");
        commandMap.registerCommand(null, new CommandForEach(this.serviceRegistry), "foreach");
        commandMap.registerCommand(null, new CommandHelp(commandMap), "help", "ask", "?");
        commandMap.registerCommand(null, new CommandInfo(this.serviceRegistry), "info", "information", "i");
        commandMap.registerCommand(null, new CommandKick(this.serviceRegistry), "kick");
        commandMap.registerCommand(null, new CommandList(this.serviceRegistry), "list", "glist");
        commandMap.registerCommand(null, new CommandSwitch(this.serviceRegistry), "switch");
        commandMap.registerCommand(null, new CommandPermissions(this.serviceRegistry), "perms");

        commandMap.registerCommand(null, new CommandAdf(), "adf");

        this.serviceRegistry.setProvider(null, CommandMap.class, commandMap, false, true);
    }

    // todo we could put information like "CPS (autoclicker), PlayerESP (is this possible in the 1.8?)" into the action bar
    //  Or maybe an extra program (like a labymod addon) or a standalone program which can be opened on a second screen (or the mobile?) to display some information
    //  Or maybe just a website for that?
    //  And add bac click limit disable

    // TODO every lightning is cached, this should not be the case

}
