package com.github.derrop.proxy.session;

import com.github.derrop.proxy.api.session.ProvidedSessionService;
import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.UUID;

public class BasicProvidedSessionService implements ProvidedSessionService {

    private static final AuthenticationService SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());

    @Override
    public @NotNull UserAuthentication login(@NotNull String userName, @NotNull String password) throws AuthenticationException {
        UserAuthentication userAuthentication = SERVICE.createUserAuthentication(Agent.MINECRAFT);
        userAuthentication.setUsername(userName);
        userAuthentication.setPassword(password);

        userAuthentication.logIn();
        return userAuthentication;
    }

    @Override
    public @NotNull MinecraftSessionService createSessionService() {
        return SERVICE.createMinecraftSessionService();
    }
}
