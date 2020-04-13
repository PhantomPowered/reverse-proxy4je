package de.derrop.minecraft.proxy;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.derklaro.minecraft.proxy.TheProxy;
import de.derklaro.minecraft.proxy.connections.basic.BasicServiceConnection;
import de.derklaro.minecraft.proxy.event.basic.DefaultEventManager;
import de.derrop.minecraft.proxy.api.Proxy;
import de.derrop.minecraft.proxy.api.chat.component.TextComponent;
import de.derrop.minecraft.proxy.api.command.CommandMap;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.event.EventManager;
import de.derrop.minecraft.proxy.api.player.PlayerRepository;
import de.derrop.minecraft.proxy.api.plugin.PluginManager;
import de.derrop.minecraft.proxy.api.task.Task;
import de.derrop.minecraft.proxy.api.task.TaskFutureListener;
import de.derrop.minecraft.proxy.api.util.MCCredentials;
import de.derrop.minecraft.proxy.api.util.NetworkAddress;
import de.derrop.minecraft.proxy.api.util.Title;
import de.derrop.minecraft.proxy.ban.BanTester;
import de.derrop.minecraft.proxy.command.ConsoleCommandSender;
import de.derrop.minecraft.proxy.command.DefaultCommandMap;
import de.derrop.minecraft.proxy.command.defaults.*;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.logging.DefaultLogger;
import de.derrop.minecraft.proxy.logging.FileLoggerHandler;
import de.derrop.minecraft.proxy.logging.ILogger;
import de.derrop.minecraft.proxy.logging.JAnsiConsole;
import de.derrop.minecraft.proxy.minecraft.AccountReader;
import de.derrop.minecraft.proxy.permission.PermissionProvider;
import de.derrop.minecraft.proxy.player.DefaultPlayerRepository;
import de.derrop.minecraft.proxy.plugin.DefaultPluginManager;
import de.derrop.minecraft.proxy.reconnect.ReconnectProfile;
import de.derrop.minecraft.proxy.replay.ReplaySystem;
import de.derrop.minecraft.proxy.storage.UUIDStorage;
import net.md_5.bungee.api.BungeeTitle;
import net.md_5.bungee.protocol.packet.KeepAlive;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MCProxy extends Proxy {

    private static MCProxy instance;

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

    private final Collection<ServiceConnection> openConnections = new CopyOnWriteArrayList<>();

    protected MCProxy() throws IOException {
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
    public BasicServiceConnection findBestConnection(ProxiedPlayer player) {
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
    public Title createTitle() {
        return new BungeeTitle();
    }

    @Override
    public ServiceConnection createConnection(MCCredentials credentials, NetworkAddress serverAddress) throws AuthenticationException {
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

        TheProxy proxy = new TheProxy();
        proxy.handleStart();
        instance.addShutdownRunnable(proxy::end);

        if (Files.exists(TheProxy.ACCOUNT_PATH)) {
            instance.accountReader.readAccounts(TheProxy.ACCOUNT_PATH, (mcCredentials, networkAddress) -> {
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
            instance.accountReader.writeDefaults(TheProxy.ACCOUNT_PATH);
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
