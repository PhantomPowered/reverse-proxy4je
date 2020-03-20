package de.derrop.minecraft.proxy.minecraft;

import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import java.net.Proxy;
import java.util.UUID;

public class SessionUtils {

    public static final AuthenticationService SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());

    public static UserAuthentication logIn(String email, String password) {
        UserAuthentication authentication = SERVICE.createUserAuthentication(Agent.MINECRAFT);
        authentication.setUsername(email);
        authentication.setPassword(password);

        try {
            authentication.logIn();

            return authentication;
        } catch (AuthenticationException exception) {
            exception.printStackTrace();
        }

        return null;
    }

}
