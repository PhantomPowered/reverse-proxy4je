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
package com.github.derrop.proxy.account;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.util.MCServiceCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.KickedException;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TODO as plugin
public class BanTester {

    private static final Path DATA_PATH = Paths.get("proxy_info.txt");

    private MCProxy proxy;
    
    private NetworkAddress[] proxies;
    private int currentProxyIndex = 0;

    public BanTester(MCProxy proxy) {
        this.proxy = proxy;
        try {
            this.init();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void init() throws IOException {
        Collection<NetworkAddress> proxies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(BanTester.class.getClassLoader().getResourceAsStream("proxies.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                NetworkAddress address = NetworkAddress.parse(line);
                if (address != null) {
                    proxies.add(address);
                }
            }
        }
        this.proxies = proxies.toArray(new NetworkAddress[0]);

        if (Files.exists(DATA_PATH)) {
            byte[] data = Files.readAllBytes(DATA_PATH);
            this.currentProxyIndex = ByteBuffer.wrap(data).getInt();
        }
    }

    private void writeIndex() {
        try {
            Files.write(DATA_PATH, ByteBuffer.allocate(4).putInt(this.currentProxyIndex).array());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isBanned(MCServiceCredentials credentials, NetworkAddress address) throws AuthenticationException {
        System.out.println("Testing if the account " + credentials.getEmail() + " is banned on " + address + "...");

        ConnectedProxyClient proxyClient = new ConnectedProxyClient(null, null);
        if (!proxyClient.performMojangLogin(credentials)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        System.out.println("Testing if " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address + "...");

        if (this.currentProxyIndex >= this.proxies.length) {
            this.currentProxyIndex = 0;
        }

        for (int i = this.currentProxyIndex; i < this.proxies.length; i++) {
            NetworkAddress proxy = this.proxies[i];

            System.out.println("Trying to connect to " + address + " as " + proxyClient.getAccountName() + " (" + credentials.getEmail() + ") through " + proxy);

            String kickReason = null;

            try {
                if (proxyClient.connect(address, proxy).get(5, TimeUnit.SECONDS)) {
                    proxyClient.close();
                    System.out.println("Account " + credentials.getEmail() + " is not banned on " + address);
                    return false;
                }

                kickReason = proxyClient.getLastKickReason() == null ? null : GsonComponentSerializer.INSTANCE.serialize(proxyClient.getLastKickReason());
            } catch (Exception exception) {
                if (exception.getCause() instanceof KickedException) {
                    kickReason = ChatColor.stripColor(exception.getMessage());
                } else if (!(exception instanceof TimeoutException)) {
                    exception.printStackTrace();
                }
            }

            proxyClient.disconnect();

            if (kickReason != null) {
                System.out.println("Account " + proxyClient.getAccountName() + " (" + credentials.getEmail() + ") got kicked while trying to check whether the account is banned on " + address + " through " + proxy + ": " + kickReason.replace('\n', ' '));
                switch (isBanned(kickReason)) {
                    case SUSPICIOUS_IP:
                        ++this.currentProxyIndex; // make later ban checks faster
                        this.writeIndex();
                        continue;

                    case NOT_BANNED:
                        return false;

                    case BANNED:
                        System.out.println("Account " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address);
                        ++this.currentProxyIndex; // prevent that more accounts get banned while connecting through this proxy
                        this.writeIndex();
                        return true;
                }
            }
        }

        throw new IllegalStateException("No proxies available");
    }

    public static BanTestResult isBanned(String kickReason) {
        if (kickReason.contains("Suspicious IP detected. More information here")) { // Gomme blocked IP
            return BanTestResult.SUSPICIOUS_IP;
        } else if (kickReason.equals("Du bist bereits auf dem Netzwerk") || kickReason.equals("Already connected to this proxy!")) {
            return BanTestResult.NOT_BANNED;
        } else if (kickReason.toLowerCase().contains("banned") || kickReason.toLowerCase().contains("gebannt")) {
            return BanTestResult.BANNED;
        } else {
            return BanTestResult.NOT_BANNED;
        }
    }

    public enum BanTestResult {
        BANNED, SUSPICIOUS_IP, NOT_BANNED;
    }

}
