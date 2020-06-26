package com.github.derrop.proxy.plugins.gommecw.labyconnect.user;

import com.mojang.authlib.GameProfile;

public class ChatRequest extends ChatUser {
    public ChatRequest(GameProfile gameProfile) {
        super(gameProfile, UserStatus.OFFLINE, "", null, 0, "", 0L, 0L, 0, false);
    }
}


