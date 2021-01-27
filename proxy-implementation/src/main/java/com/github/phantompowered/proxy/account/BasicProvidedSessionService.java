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

import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.ProvidedSessionService;
import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BasicProvidedSessionService implements ProvidedSessionService {

    private static final AuthenticationService SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());

    private final SessionCache cache;

    public BasicProvidedSessionService(ServiceRegistry registry) {
        this.cache = new SessionCache(registry);
    }

    @Override
    public @NotNull UserAuthentication login(@NotNull String userName, @NotNull String password) throws AuthenticationException {
        CachedUserAuthentication cached = this.cache.getCachedAuthentication(userName, password);
        if (cached != null) {
            if (this.isValid(cached)) {
                return cached;
            }
            this.cache.remove(userName, password);
        }

        UserAuthentication userAuthentication = SERVICE.createUserAuthentication(Agent.MINECRAFT);
        userAuthentication.setUsername(userName);
        userAuthentication.setPassword(password);

        userAuthentication.logIn();

        this.cache.cache(userName, password, CachedUserAuthentication.fromAuthentication(userAuthentication));

        return userAuthentication;
    }

    @Override
    public boolean isValid(@NotNull UserAuthentication authentication) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://authserver.mojang.com/validate").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                JsonObject object = new JsonObject();
                object.addProperty("accessToken", authentication.getAuthenticatedToken());
                outputStream.write(object.toString().getBytes(StandardCharsets.UTF_8));
            }

            return connection.getResponseCode() == 204;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public @NotNull MinecraftSessionService createSessionService() {
        return SERVICE.createMinecraftSessionService();
    }
}
