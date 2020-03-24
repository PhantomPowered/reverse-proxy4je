package de.derrop.minecraft.proxy.ban;

import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BanTester {

    private static final NetworkAddress[] PROXIES = Arrays.stream((
            "216.144.228.130:15378\n" +
            "174.76.48.233:4145\n" +
            "216.144.230.233:15993\n" +
            "184.178.172.18:15280\n" +
            "134.209.100.103:49616\n" +
            "174.76.35.29:36177\n" +
            "174.76.48.249:4145\n" +
            "166.62.43.205:49683\n" +
            "104.238.97.215:14619\n" +
            "184.178.172.28:15294\n" +
            "174.75.238.68:16399\n" +
            "174.75.238.87:16412\n" +
            "173.245.239.223:16938\n" +
            "142.93.51.220:22270\n" +
            "64.90.48.88:44178\n" +
            "192.169.139.161:8975\n" +
            "174.76.48.228:4145\n" +
            "166.62.43.205:60226\n" +
            "174.76.48.252:4145\n" +
            "184.176.166.8:17864\n" +
            "174.75.238.93:16406\n" +
            "70.166.38.71:24801\n" +
            "66.110.216.221:39603\n" +
            "166.62.118.88:17993\n" +
            "69.163.163.26:3578\n" +
            "166.62.43.174:48007\n" +
            "66.135.227.178:4145\n" +
            "167.71.146.116:9050\n" +
            "184.178.172.5:15303\n" +
            "184.178.172.25:15291\n" +
            "72.210.252.152:46154\n" +
            "72.49.49.11:31034\n" +
            "132.148.159.44:31462\n" +
            "98.162.25.29:31679\n" +
            "45.55.159.57:54985\n" +
            "69.61.200.104:36181\n" +
            "70.166.38.80:24822\n" +
            "103.209.64.19:6667\n" +
            "24.249.199.14:57335\n" +
            "104.238.111.150:38702\n" +
            "72.221.164.35:60670\n" +
            "69.163.160.197:50301\n" +
            "103.240.161.101:6667\n" +
            "103.216.82.216:6667\n" +
            "103.250.166.4:6667\n" +
            "103.216.82.190:6667\n" +
            "103.240.161.108:6667\n" +
            "103.251.225.16:6667\n" +
            "106.14.242.17:60341\n" +
            "106.14.243.103:49323\n" +
            "178.128.154.236:24629\n" +
            "178.128.154.236:40614\n" +
            "178.128.154.236:46092\n" +
            "178.128.154.236:6786\n" +
            "159.89.162.107:50867\n" +
            "198.11.179.15:80\n" +
            "182.93.84.130:23649\n" +
            "198.11.179.15:8080\n" +
            "182.93.84.130:52104\n" +
            "185.164.72.76:1080\n" +
            "139.99.104.233:5467\n" +
            "174.70.241.18:24404\n" +
            "36.255.222.21:1080\n" +
            "208.113.221.3:63173\n" +
            "178.128.154.236:10078\n" +
            "192.169.154.145:4661\n" +
            "175.184.232.62:6667\n" +
            "166.62.43.174:18834\n" +
            "166.62.43.174:46161\n" +
            "192.169.154.145:58200\n" +
            "45.55.159.57:22743\n" +
            "45.55.159.57:9359\n" +
            "27.116.51.92:6667\n" +
            "190.214.20.10:9999\n" +
            "43.224.8.12:6667\n" +
            "27.116.51.119:6667\n" +
            "47.100.240.237:1080\n" +
            "69.163.162.174:58483\n" +
            "69.163.166.43:43550\n" +
            "95.110.194.245:55402\n" +
            "95.110.194.245:35740\n" +
            "50.62.61.96:35802\n" +
            "64.118.86.51:63960\n" +
            "69.163.161.180:3578\n" +
            "69.163.161.110:55751\n" +
            "91.98.103.154:1080\n" +
            "95.110.194.245:26518\n" +
            "91.93.188.227:1080\n" +
            "69.163.167.133:43550\n" +
            "69.163.162.98:49711\n" +
            "50.62.31.62:28057\n" +
            "64.118.86.52:53973\n" +
            "69.163.162.134:55751\n" +
            "103.241.227.107:6667\n" +
            "104.238.97.163:35190\n" +
            "69.163.162.196:58483\n" +
            "103.251.214.167:6667\n" +
            "103.216.82.18:6667\n" +
            "103.216.82.20:6667\n" +
            "103.250.166.17:6667\n" +
            "103.250.166.12:6667\n" +
            "106.14.243.103:53368\n" +
            "178.62.206.174:8080\n" +
            "178.128.154.236:43337\n" +
            "139.99.104.233:58880\n" +
            "139.99.104.233:12804\n" +
            "213.136.89.190:38816\n" +
            "208.113.223.133:63173\n" +
            "61.41.9.213:10081\n" +
            "166.62.43.174:28561\n" +
            "198.12.157.28:18199\n" +
            "66.33.212.17:13959\n" +
            "69.163.163.175:43550\n" +
            "69.163.163.179:50301\n" +
            "208.113.154.52:63173\n" +
            "159.89.162.107:3206\n" +
            "103.216.82.198:6667\n" +
            "50.62.61.96:13984\n" +
            "64.90.54.192:44178\n" +
            "66.33.210.98:9080\n" +
            "69.163.160.61:44427\n" +
            "69.163.160.162:49711\n" +
            "69.163.162.174:58763\n" +
            "69.163.166.122:55751\n" +
            "150.129.201.30:6667\n" +
            "109.226.220.93:9999\n" +
            "103.216.82.52:6667\n" +
            "80.211.240.241:1085\n" +
            "103.241.227.110:6667\n" +
            "103.216.82.199:6667\n" +
            "167.172.19.226:8902\n" +
            "159.89.162.107:17921\n" +
            "69.163.161.156:46199\n" +
            "103.216.82.196:6667\n" +
            "103.241.227.118:6667\n" +
            "45.32.22.125:9050\n" +
            "150.129.151.42:6667\n" +
            "139.59.90.148:4760\n" +
            "157.119.207.10:6667\n" +
            "149.56.172.30:61228\n" +
            "50.62.31.62:44889\n" +
            "188.166.22.22:9050\n" +
            "64.118.87.11:63960\n" +
            "69.163.162.160:44427\n" +
            "69.163.162.134:46199\n" +
            "150.129.54.111:6667\n" +
            "173.236.177.130:53543\n" +
            "103.216.82.19:6667\n" +
            "103.216.82.37:6667\n" +
            "69.163.160.249:46813\n" +
            "43.224.8.121:6667\n" +
            "69.163.166.7:58763\n" +
            "68.180.105.55:10200\n" +
            "69.163.162.191:43167\n" +
            "69.163.164.77:49711\n" +
            "64.118.86.55:53973\n" +
            "69.163.167.136:49711\n" +
            "103.21.163.76:6667\n" +
            "103.241.227.100:6667\n" +
            "122.4.199.31:1081").split("\n")).map(NetworkAddress::parse).filter(Objects::nonNull).toArray(NetworkAddress[]::new);

    private int currentProxyIndex = 0;

    public boolean isBanned(MCCredentials credentials, NetworkAddress address) {
        System.out.println("Testing if the account " + credentials.getEmail() + " is banned on " + address + "...");

        ConnectedProxyClient proxyClient = new ConnectedProxyClient();
        if (!proxyClient.performMojangLogin(credentials)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        System.out.println("Testing if " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address + "...");

        for (int i = this.currentProxyIndex; i < PROXIES.length; i++) {
            NetworkAddress proxy = PROXIES[i];

            System.out.println("Trying to connect to " + address + " as " + proxyClient.getAccountName() + " (" + credentials.getEmail() + ") through " + proxy);

            try {
                if (proxyClient.connect(address, proxy).get(10, TimeUnit.SECONDS)) {
                    proxyClient.getChannelWrapper().close();
                    System.out.println("Client " + credentials.getEmail() + " is not banned on " + address);
                    return false;
                }
                if (proxyClient.getLastKickReason() != null) {
                    String kickReason = TextComponent.toPlainText(proxyClient.getLastKickReason());
                    System.out.println("Account " + proxyClient.getAccountName() + " (" + credentials.getEmail() + ") got kicked while trying to check whether the account is banned on " + address + " through " + proxy + ": " + kickReason);
                    if (kickReason.contains("Suspicious IP detected. More information here")) { // Gomme blocked IP
                        ++this.currentProxyIndex;
                        continue;
                    } else if (kickReason.equals("Du bist bereits auf dem Netzwerk") || kickReason.equals("Already connected to this proxy!")) {
                        return false;
                    } else if (kickReason.toLowerCase().contains("banned") || kickReason.toLowerCase().contains("gebannt")) {
                        System.out.println("Account " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address + "...");
                        return true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            System.err.println("Failed to test if " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address + " with proxy " + proxy);
            ++this.currentProxyIndex; // if the account is banned, we don't use this proxy anymore to prevent that more accounts get banned
        }
        return false;
    }

}
