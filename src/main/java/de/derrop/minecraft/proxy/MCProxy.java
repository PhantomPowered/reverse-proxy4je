package de.derrop.minecraft.proxy;

import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.defaults.*;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.connection.velocity.PlayerVelocityHandler;
import de.derrop.minecraft.proxy.minecraft.AccountReader;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.permission.PermissionProvider;
import de.derrop.minecraft.proxy.storage.UUIDStorage;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
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

    private MCProxy() {
        this.commandMap.registerCommand(new CommandHelp(this.commandMap));
        this.commandMap.registerCommand(new CommandInfo());
        this.commandMap.registerCommand(new CommandSwitch());
        this.commandMap.registerCommand(new CommandList());
        this.commandMap.registerCommand(new CommandChat());
        this.commandMap.registerCommand(new CommandAlert());
        this.commandMap.registerCommand(new CommandForEach());
        this.commandMap.registerCommand(new CommandPermissions());
        this.commandMap.registerCommand(new CommandConnect());// todo this doesn't work, but a command like "add account <email:password> <server>" and "disconnect account <name>" would be useful
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

        return proxyClient.connect(address).get();
    }

    public void removeProxyClient(ConnectedProxyClient proxyClient) {
        this.onlineClients.remove(proxyClient);
    }

    public ConnectedProxyClient findBestProxyClient() {
        return this.onlineClients.stream().filter(proxyClient -> proxyClient.getRedirector() == null).findFirst().orElse(null);
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
            instance.accountReader.readAccounts(accountsPath, (credentials, address) -> {
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

        new Thread(() -> {
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
            }
        }).start();

        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    // todo we could put information like "CPS (autoclicker)" into the action bar

}
