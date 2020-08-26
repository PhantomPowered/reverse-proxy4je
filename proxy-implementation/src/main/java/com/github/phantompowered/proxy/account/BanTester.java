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
package com.github.phantompowered.proxy.account;

import com.github.phantompowered.proxy.api.chat.ChatColor;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.KickedException;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TODO: as plugin
public class BanTester {

    private static final Path DATA_PATH = Paths.get("proxy_info.txt");

    private final NetworkAddress[] proxies = new NetworkAddress[0]; // TODO
    private int currentProxyIndex = 0;

    public BanTester() {
        try {
            this.init();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
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

    public void init() throws IOException {
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

    public boolean isBanned(ServiceRegistry serviceRegistry, MCServiceCredentials credentials, NetworkAddress address) throws AuthenticationException {
        System.out.println("Testing if the account " + credentials.getEmail() + " is banned on " + address + "...");

        ConnectedProxyClient proxyClient = new ConnectedProxyClient(serviceRegistry, null);
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

                kickReason = proxyClient.getLastKickReason() == null ? null : GsonComponentSerializer.gson().serialize(proxyClient.getLastKickReason());
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

                    case BANNED:
                        System.out.println("Account " + proxyClient.getAccountName() + "#" + proxyClient.getAccountUUID() + " (" + credentials.getEmail() + ") is banned on " + address);
                        ++this.currentProxyIndex; // prevent that more accounts get banned while connecting through this proxy
                        this.writeIndex();
                        return true;

                    default:
                        return false;
                }
            }
        }

        throw new IllegalStateException("No proxies available");
    }

    public enum BanTestResult {
        BANNED, SUSPICIOUS_IP, NOT_BANNED
    }

}
