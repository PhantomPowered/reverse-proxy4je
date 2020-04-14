package com.github.derrop.proxy.api.session;

import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import org.jetbrains.annotations.NotNull;

public interface ProvidedSessionService {

    @NotNull
    UserAuthentication login(@NotNull String userName, @NotNull String password) throws AuthenticationException;

    @NotNull MinecraftSessionService createSessionService();
}
