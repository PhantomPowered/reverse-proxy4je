package com.github.phantompowered.proxy.api.events.connection.service.playerinfo;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.events.connection.service.ServiceConnectionEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInfoUpdateEvent extends ServiceConnectionEvent {

    private final PlayerInfo playerInfo;

    public PlayerInfoUpdateEvent(@NotNull ServiceConnection connection, PlayerInfo playerInfo) {
        super(connection);
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
