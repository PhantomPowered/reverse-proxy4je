package de.derrop.minecraft.proxy;

import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.defaults.*;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MCProxy {

    private static MCProxy instance;

    private ProxyServer proxyServer = new ProxyServer();
    private Collection<ConnectedProxyClient> onlineClients = new CopyOnWriteArrayList<>();
    private CommandMap commandMap = new CommandMap();

    private MCProxy() {
        this.commandMap.registerCommand(new CommandHelp(this.commandMap));
        this.commandMap.registerCommand(new CommandInfo());
        this.commandMap.registerCommand(new CommandSwitch());
        this.commandMap.registerCommand(new CommandList());
        this.commandMap.registerCommand(new CommandChat());
        this.commandMap.registerCommand(new CommandAlert());
        this.commandMap.registerCommand(new CommandForEach());
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

    public Collection<ConnectedProxyClient> getFreeClients() {
        return this.getOnlineClients().stream().filter(proxyClient -> proxyClient.getRedirector() == null).collect(Collectors.toList());
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public static MCProxy getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        instance = new MCProxy();
        instance.proxyServer.start(new InetSocketAddress(25565));
        String creds =
                "SELLIpart1:syreha.a.allen@hotmail.co.uk:Reya0606\n" +
                        "Prancine:mochatoby324@gmail.com:Ilovepickles123\n" +
                        "Streetfighter02:milaarre@hotmail.com:Hudson2006\n" +
                        "notvaleria:lilyblue1228@yahoo.com:shawn1228\n" +
                        "Kaylah_mc:kaylahneowhouse@gmail.com:aussie01\n" +
                        "stephskiii:stephskinoosh@gmail.com:chloemax1\n" +
                        "vickysurf:vickysurf11@gmail.com:50sombras\n" +
                        "Staticfrowizzdra:kealicie@gmail.com:flylikeaG6\n" +
                        "a:86baby86a@gmail.com:donald86";
        for (String s : creds.split("\n")) {
            String[] split = s.split(":");
            String s1 = split[1] + ":" + split[2];
            System.out.println(instance.startClient(new NetworkAddress("49.12.37.57", 25577), MCCredentials.parse(s1)));
            //System.out.println(instance.startClient(new NetworkAddress("mc.gommehd.com", 25565), MCCredentials.parse(s1)));
            Thread.sleep(1000);
        }
        MCCredentials credentials = MCCredentials.parse("superfini@gmx.net:lilli02");
        //System.out.println(instance.startClient(new NetworkAddress("localhost", 25566), credentials));
        //System.out.println(instance.startClient(new NetworkAddress("mc.gommehd.com", 25565), credentials));

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
