package com.github.derrop.proxy;

import com.github.derrop.proxy.account.AccountBiConsumer;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockStateRegistry;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.player.PlayerRepository;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.ban.BanTester;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.block.DefaultBlockStateRegistry;
import com.github.derrop.proxy.brand.ProxyBrandChangeListener;
import com.github.derrop.proxy.command.DefaultCommandMap;
import com.github.derrop.proxy.command.defaults.*;
import com.github.derrop.proxy.connection.ProxyServer;
import com.github.derrop.proxy.entity.EntityTickHandler;
import com.github.derrop.proxy.event.DefaultEventManager;
import com.github.derrop.proxy.logging.ILogger;
import com.github.derrop.proxy.minecraft.AccountReader;
import com.github.derrop.proxy.permission.PermissionProvider;
import com.github.derrop.proxy.player.DefaultPlayerRepository;
import com.github.derrop.proxy.plugin.DefaultPluginManager;
import com.github.derrop.proxy.reconnect.ReconnectProfile;
import com.github.derrop.proxy.service.BasicServiceRegistry;
import com.github.derrop.proxy.storage.UUIDStorage;
import com.github.derrop.proxy.title.BasicTitle;
import com.mojang.authlib.exceptions.AuthenticationException;
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
    private UUIDStorage uuidStorage = new UUIDStorage();
    private AccountReader accountReader = new AccountReader();
    private EventManager eventManager = new DefaultEventManager();
    private PlayerRepository playerRepository = new DefaultPlayerRepository(this);
    private PluginManager pluginManager = new DefaultPluginManager(this);

    private BanTester banTester = new BanTester();

    private Collection<BasicServiceConnection> onlineClients = new CopyOnWriteArrayList<>();
    private Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    private ILogger logger;

    private final Collection<Runnable> shutdownHooks = new CopyOnWriteArrayList<>();

    protected MCProxy(@NotNull ILogger logger) throws IOException {
        instance = this;
        this.serviceRegistry.setProvider(null, Proxy.class, this, true);
        this.serviceRegistry.setProvider(null, BlockStateRegistry.class, new DefaultBlockStateRegistry(), true);

        this.logger = logger;
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
    public @Nullable BasicServiceConnection findBestConnection(ProxiedPlayer player) {
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

    public Map<UUID, ReconnectProfile> getReconnectProfiles() {
        return reconnectProfiles;
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

    public void bootstrap(int port) throws IOException {
        this.proxyServer.start(new InetSocketAddress(port)); // TODO: service + config

        this.pluginManager.loadPlugins(Paths.get("plugins")); // TODO: service + config
        this.pluginManager.enablePlugins();

        this.handleCommands();

        this.eventManager.registerListener(new ProxyBrandChangeListener()); // TODO: service
        if (Files.notExists(ACCOUNT_PATH)) {
            this.accountReader.writeDefaults(ACCOUNT_PATH);
        }

        this.accountReader.readAccounts(ACCOUNT_PATH, new AccountBiConsumer());
        EntityTickHandler.startTick();
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

        this.serviceRegistry.setProvider(null, CommandMap.class, commandMap);
    }

    // todo we could put information like "CPS (autoclicker), PlayerESP (is this possible in the 1.8?)" into the action bar
    //  Or maybe an extra program (like a labymod addon) or a standalone program which can be opened on a second screen (or the mobile?) to display some information
    //  Or maybe just a website for that?
    //  And add bac click limit disable
    //  Service registry

}
