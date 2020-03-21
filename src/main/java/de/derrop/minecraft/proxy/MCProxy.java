package de.derrop.minecraft.proxy;

import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.defaults.*;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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
        this.commandMap.registerCommand(new CommandInfo());
        this.commandMap.registerCommand(new CommandSwitch());
        this.commandMap.registerCommand(new CommandList());
        this.commandMap.registerCommand(new CommandChat());
        this.commandMap.registerCommand(new CommandAlert());
        this.commandMap.registerCommand(new CommandForEach());
        //this.commandMap.registerCommand(new CommandConnect()); todo this doesn't work, but a command like "add account <email:password> <server>" and "disconnect account <name>" would be useful

        // todo help command?
    }

    public boolean startClient(NetworkAddress address, MCCredentials credentials) throws ExecutionException, InterruptedException {
        ConnectedProxyClient proxyClient = new ConnectedProxyClient();

        if (!proxyClient.performMojangLogin(credentials)) {
            return false;
        }

        boolean success = proxyClient.connect(address).get();
        if (success) {
            this.onlineClients.add(proxyClient);
        }

        return success;
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
                "MirLulu:yasmine.fleurbaaij@kpnmail.nl:Sarah123\n" +
                "NotJxey:karty09@gmail.com:puppy1125\n" +
                /*// "TvdAddicted:elisablair251@gmail.com:turgeon77\n" + -> Gomme PERMANENT banned
                "kayleighblueeyes:mindy86@hotmail.co.uk:kayleigh2004\n" +
                "Aftdarksummer:aftdarksummer@gmail.com:Moneymaker123\n" +
                "DxNicolexD:nicole.nterekas@hotmail.com:jhutch1992\n" +
                "EmmaBean100:emma_belle@sbcglobal.net:emmascool01\n" +
                "GoldiieLocks:angolden.4@gmail.com:Softball19\n" +
                "Softeasi:sammyskater02@gmail.com:savsam22\n" +
                "NinjaGirl47:amytsivis@yahoo.com:sweetpea426\n" +
                "melowe101:wagonlandera@gmail.com:KittyKat101\n" +
                "SELLIpart1:syreha.a.allen@hotmail.co.uk:Reya0606\n" +
                "Prancine:mochatoby324@gmail.com:Ilovepickles123\n" +
                "Streetfighter02:milaarre@hotmail.com:Hudson2006\n" +
                "jessixameow:xjessicamero@gmail.com:jessica123\n" +
                "TAMA815:danabelle360@gmail.com:cosmo123\n" +
                "fini20002:superfini@gmx.net:lilli02\n" +
                "rockinruru:ruthieru1@aol.com:MochaDog1\n" +
                "nomocker:noriane-mcr@hotmail.fr:twilight33\n" +
                "abbbiii2711:saxkb@yahoo.co.uk:maxjosh1d123\n" +
                "hackhornet1:npickl@me.com:2smart4u\n" +*/
                "Twihard2097:sarahkeatley@shaw.ca:Skwk1997\n" +
                "Madp03:chipmunkgirl10@gmail.com:peanut10\n" +
                "SeaShel:slb224@live.com:96pisces";
        for (String s : creds.split("\n")) {
            String[] split = s.split(":");
            String s1 = split[1] + ":" + split[2];
            System.out.println(instance.startClient(new NetworkAddress("mc.gommehd.com", 25565), MCCredentials.parse(s1)));
            //System.out.println(instance.startClient(new NetworkAddress("49.12.37.57", 25565), MCCredentials.parse(s1)));
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
