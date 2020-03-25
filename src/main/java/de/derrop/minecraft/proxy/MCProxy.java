package de.derrop.minecraft.proxy;

import de.derrop.minecraft.proxy.ban.BanTester;
import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.ConsoleCommandSender;
import de.derrop.minecraft.proxy.command.defaults.*;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.logging.DefaultLogger;
import de.derrop.minecraft.proxy.logging.FileLoggerHandler;
import de.derrop.minecraft.proxy.logging.ILogger;
import de.derrop.minecraft.proxy.logging.JAnsiConsole;
import de.derrop.minecraft.proxy.minecraft.AccountReader;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.permission.PermissionProvider;
import de.derrop.minecraft.proxy.reconnect.ReconnectProfile;
import de.derrop.minecraft.proxy.storage.UUIDStorage;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MCProxy {

    private static MCProxy instance;

    private ProxyServer proxyServer = new ProxyServer();
    private PermissionProvider permissionProvider = new PermissionProvider();
    private UUIDStorage uuidStorage = new UUIDStorage();
    private AccountReader accountReader = new AccountReader();
    private CommandMap commandMap = new CommandMap();

    private Collection<ConnectedProxyClient> onlineClients = new CopyOnWriteArrayList<>();
    private Map<UUID, ReconnectProfile> reconnectProfiles = new ConcurrentHashMap<>();

    private ILogger logger;

    private MCProxy() throws IOException {
        this.logger = new DefaultLogger(new JAnsiConsole(() -> String.format("&c%s&7@&fProxy &7> &e", System.getProperty("user.name"))));
        this.logger.addHandler(new FileLoggerHandler("logs/proxy.log", 8_000_000));
        this.logger.getConsole().addLineHandler("DefaultCommandMap", line ->
                this.commandMap.dispatchCommand(new ConsoleCommandSender(this.logger), CommandMap.PREFIX + line)
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
        this.commandMap.registerCommand(new CommandStop());

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Thread"));
    }

    public boolean startClient(NetworkAddress address, MCCredentials credentials) throws ExecutionException, InterruptedException {
        ConnectedProxyClient proxyClient = new ConnectedProxyClient();

        try {
            if (!proxyClient.performMojangLogin(credentials)) {
                return false;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }

        return proxyClient.connect(address, null).get();
    }

    public void switchClientSafe(ProxiedPlayer player, ConnectedProxyClient proxyClient) {
        player.disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Reconnect within the next 60 seconds to be connected with " + proxyClient.getAccountName()));
        MCProxy.getInstance().setReconnectTarget(player.getUniqueId(), proxyClient.getAccountUUID());
    }

    public void removeProxyClient(ConnectedProxyClient proxyClient) {
        this.onlineClients.remove(proxyClient);
    }

    public ConnectedProxyClient findBestProxyClient(UUID uniqueId) {
        if (uniqueId != null && this.reconnectProfiles.containsKey(uniqueId)) {
            ReconnectProfile profile = this.reconnectProfiles.get(uniqueId);
            if (System.currentTimeMillis() < profile.getTimeout()) {
                Optional<ConnectedProxyClient> optionalClient = this.onlineClients.stream()
                        .filter(proxyClient -> proxyClient.getRedirector() == null)
                        .filter(proxyClient -> proxyClient.getAccountUUID().equals(profile.getTargetUniqueId()))
                        .findFirst();
                if (optionalClient.isPresent()) {
                    this.reconnectProfiles.remove(uniqueId);
                    return optionalClient.get();
                }
            }
        }
        return this.onlineClients.stream()
                .filter(proxyClient -> proxyClient.getRedirector() == null)
                .filter(proxyClient -> !this.reconnectProfiles.containsKey(proxyClient.getAccountUUID()))
                .findFirst().orElse(null);
    }

    public void setReconnectTarget(UUID uniqueId, UUID targetUniqueId) {
        this.reconnectProfiles.put(uniqueId, new ReconnectProfile(uniqueId, targetUniqueId));
    }

    public Collection<ConnectedProxyClient> getOnlineClients() {
        return onlineClients;
    }

    public Collection<ProxiedPlayer> getOnlinePlayers() {
        return this.getOnlineClients().stream().map(ConnectedProxyClient::getRedirector).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public ProxiedPlayer getOnlinePlayer(UUID uniqueId) {
        return this.getOnlineClients().stream().map(ConnectedProxyClient::getRedirector).filter(Objects::nonNull).filter(connection -> connection.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    public Collection<ConnectedProxyClient> getFreeClients() {
        return this.getOnlineClients().stream().filter(proxyClient -> proxyClient.getRedirector() == null).collect(Collectors.toList());
    }

    public void shutdown() {
        for (ConnectedProxyClient onlineClient : this.getOnlineClients()) {
            if (onlineClient.getRedirector() != null) {
                onlineClient.getRedirector().disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + "Shutting down the proxy..."));
            }
            onlineClient.disconnect();
        }
        if (!Thread.currentThread().getName().equals("Shutdown Thread")) {
            System.exit(0);
        }
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public PermissionProvider getPermissionProvider() {
        return permissionProvider;
    }

    public UUIDStorage getUUIDStorage() {
        return uuidStorage;
    }

    public static MCProxy getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        instance = new MCProxy();
        instance.proxyServer.start(new InetSocketAddress(25565));

        Path accountsPath = Paths.get("accounts.txt");
        if (Files.exists(accountsPath)) {
            BanTester banTester = new BanTester();
            instance.accountReader.readAccounts(accountsPath, (credentials, address) -> {
                /*try { // TODO
                    if (banTester.isBanned(credentials, address)) {
                        System.err.println("Account " + credentials.getEmail() + " is banned on " + address + "!");
                        return;
                    }
                } catch (IllegalArgumentException exception) {
                    System.err.println("Invalid credentials for " + credentials.getEmail() + "!");
                    return;
                }*/
                try {
                    System.out.println("Connection for " + credentials.getEmail() + ": " + instance.startClient(address, credentials));
                    Thread.sleep(1000);
                } catch (ExecutionException | InterruptedException exception) {
                    exception.printStackTrace();
                }
            });
        } else {
            instance.accountReader.writeDefaults(accountsPath);
        }

         //PlayerVelocityHandler.start(); todo

        Constants.EXECUTOR_SERVICE.execute(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                for (ConnectedProxyClient onlineClient : instance.onlineClients) {
                    onlineClient.keepAliveTick();
                    if (onlineClient.getRedirector() != null) {
                        onlineClient.getRedirector().unsafe().sendPacket(new KeepAlive(System.nanoTime()));
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

}
