package de.derrop.minecraft.proxy;

import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.connection.ProxyServer;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MCProxy {

    private static MCProxy instance;

    private ProxyServer proxyServer = new ProxyServer();
    private Collection<ConnectedProxyClient> onlineClients = new ArrayList<>();

    private MCProxy() {

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

    public static MCProxy getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        instance = new MCProxy();
        instance.proxyServer.start(new InetSocketAddress(25565));
        //System.out.println(instance.startClient(new NetworkAddress("localhost", 25566), MCCredentials.parse("kealicie@gmail.com:flylikeaG6")));
        System.out.println(instance.startClient(new NetworkAddress("mc.gommehd.com", 25565), MCCredentials.parse("kealicie@gmail.com:flylikeaG6")));

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

}
