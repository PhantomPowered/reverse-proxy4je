package com.github.derrop.proxy;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.player.PlayerRepository;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.ban.BanTester;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.basic.DefaultEventManager;
import com.github.derrop.proxy.brand.ProxyBrandChangeListener;
import com.github.derrop.proxy.command.ConsoleCommandSender;
import com.github.derrop.proxy.command.DefaultCommandMap;
import com.github.derrop.proxy.command.defaults.*;
import com.github.derrop.proxy.connection.ProxyServer;
import com.github.derrop.proxy.logging.DefaultLogger;
import com.github.derrop.proxy.logging.FileLoggerHandler;
import com.github.derrop.proxy.logging.ILogger;
import com.github.derrop.proxy.logging.JAnsiConsole;
import com.github.derrop.proxy.minecraft.AccountReader;
import com.github.derrop.proxy.permission.PermissionProvider;
import com.github.derrop.proxy.player.DefaultPlayerRepository;
import com.github.derrop.proxy.plugin.DefaultPluginManager;
import com.github.derrop.proxy.reconnect.ReconnectProfile;
import com.github.derrop.proxy.replay.ReplaySystem;
import com.github.derrop.proxy.service.BasicServiceRegistry;
import com.github.derrop.proxy.storage.UUIDStorage;
import com.github.derrop.proxy.title.BasicTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.md_5.bungee.protocol.packet.KeepAlive;
import org.jetbrains.annotations.NotNull;

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
    private UUIDStorage uuidStorage = new UUIDStorage();
    private AccountReader accountReader = new AccountReader();
    private EventManager eventManager = new DefaultEventManager();
    private PlayerRepository playerRepository = new DefaultPlayerRepository(this);
    private PluginManager pluginManager = new DefaultPluginManager(this);

    private BanTester banTester = new BanTester();

    private Collection<BasicServiceConnection> onlineClients = new CopyOnWriteArrayList<>();
    private Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    private ReplaySystem replaySystem = new ReplaySystem();

    private CommandMap commandMap;
    private ILogger logger;

    private final Collection<Runnable> shutdownHooks = new CopyOnWriteArrayList<>();

    protected MCProxy() throws IOException {
        this.serviceRegistry.setProvider(null, Proxy.class, this, true);

        this.logger = new DefaultLogger(new JAnsiConsole(() -> String.format("&c%s&7@&fProxy &7> &e", System.getProperty("user.name"))));
        this.commandMap = new DefaultCommandMap(this.logger);

        this.logger.addHandler(new FileLoggerHandler("logs/proxy.log", 8_000_000));
        this.logger.getConsole().addLineHandler("DefaultCommandMap", line ->
                this.commandMap.dispatchCommand(new ConsoleCommandSender(this.logger), DefaultCommandMap.PREFIX + line)
        );

        this.commandMap.registerCommand(new CommandHelp(this.commandMap));
        this.commandMap.registerCommand(new CommandInfo());
        this.commandMap.registerCommand(new CommandSwitch());
        this.commandMap.registerCommand(new CommandList());
        this.commandMap.registerCommand(new CommandChat());
        this.commandMap.registerCommand(new CommandAlert());
        this.commandMap.registerCommand(new CommandForEach());
        this.commandMap.registerCommand(new CommandPermissions());
        this.commandMap.registerCommand(new CommandConnect());
        this.commandMap.registerCommand(new CommandAccount());
        this.commandMap.registerCommand(new CommandStop());
        this.commandMap.registerCommand(new CommandKick());
        this.commandMap.registerCommand(new CommandReplay());

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Thread"));
    }

    public void switchClientSafe(ProxiedPlayer player, ServiceConnection proxyClient) {
        player.disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + proxyClient.getName()));
        this.setReconnectTarget(player.getUniqueId(), proxyClient.getUniqueId());
    }

    public void unregisterConnection(ServiceConnection proxyClient) {
        this.onlineClients.remove(proxyClient);
    }

    @Override
    public @NotNull BasicServiceConnection findBestConnection(ProxiedPlayer player) {
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

    public ReplaySystem getReplaySystem() {
        return this.replaySystem;
    }

    public void addShutdownRunnable(@NotNull Runnable runnable) {
        this.shutdownHooks.add(runnable);
    }

    public void shutdown() {
        if (!Thread.currentThread().getName().equals("Shutdown Thread")) {
            System.exit(0);
        } else {
            for (Runnable shutdownHook : this.shutdownHooks) {
                shutdownHook.run();
            }

            this.pluginManager.disablePlugins();

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

    @NotNull
    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @NotNull
    @Override
    public CommandMap getCommandMap() {
        return this.commandMap;
    }

    @NotNull
    @Override
    public PlayerRepository getPlayerRepository() {
        return this.playerRepository;
    }

    @NotNull
    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
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

    public UUIDStorage getUUIDStorage() {
        return uuidStorage;
    }

    public BanTester getBanTester() {
        return banTester;
    }

    public ILogger getLogger() {
        return logger;
    }

    public static MCProxy getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        instance = new MCProxy();
        instance.proxyServer.start(new InetSocketAddress(25567));

        instance.pluginManager.loadPlugins(Paths.get("plugins"));
        instance.pluginManager.enablePlugins();

        instance.eventManager.registerListener(new ProxyBrandChangeListener());

        if (Files.exists(ACCOUNT_PATH)) {
            instance.accountReader.readAccounts(ACCOUNT_PATH, (mcCredentials, networkAddress) -> {
                try {
                    ServiceConnection connection = new BasicServiceConnection(instance, mcCredentials, networkAddress);

                    connection.connect(new TaskFutureListener<Boolean>() {
                        @Override
                        public void onCancel(@NotNull Task<Boolean> task) {
                            System.err.println("Connection to " + connection.getServerAddress() + " cancelled");
                        }

                        @Override
                        public void onFailure(@NotNull Task<Boolean> task) {
                            Throwable lastException = task.getException();
                            if (lastException == null) {
                                System.err.println("Got kicked from " + connection.getServerAddress() + " as " + connection.getCredentials());
                                return;
                            }

                            System.err.println("Got kicked from " + connection.getServerAddress()
                                    + " as " + connection.getCredentials() + ": " + lastException.getMessage().replace('\n', ' '));
                        }

                        @Override
                        public void onSuccess(@NotNull Task<Boolean> task) {
                            Boolean result = task.getResult();
                            if (result != null && result) {
                                System.out.println("Successfully opended connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
                                return;
                            }

                            System.err.println("Unable to open connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
                        }
                    });

                } catch (AuthenticationException exception) {
                    exception.printStackTrace();
                }
            });
        } else {
            instance.accountReader.writeDefaults(ACCOUNT_PATH);
        }

        //PlayerVelocityHandler.start(); todo

        // TODO Fix this shit
        Constants.EXECUTOR_SERVICE.execute(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                for (BasicServiceConnection onlineClient : instance.onlineClients) {
                    onlineClient.getClient().keepAliveTick();
                    if (onlineClient.getPlayer() != null) {
                        onlineClient.getPlayer().sendPacket(new KeepAlive(System.nanoTime())); // todo if no result for this packet is returned, disconnect the player
                    }
                }

                for (ReconnectProfile profile : instance.reconnectProfiles.values()) {
                    if (System.currentTimeMillis() >= profile.getTimeout()) {
                        instance.reconnectProfiles.remove(profile.getUniqueId());
                    }
                }
            }
        });

        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    // todo we could put information like "CPS (autoclicker), PlayerESP (is this possible in the 1.8?)" into the action bar
    //  Or maybe an extra program (like a labymod addon) or a standalone program which can be opened on a second screen (or the mobile?) to display some information
    //  Or maybe just a website for that?
    //  And add bac click limit disable
    //  Service registry

}
