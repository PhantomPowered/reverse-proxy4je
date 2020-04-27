package com.github.derrop.proxy.api.events.connection.service.playerinfo;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.player.PlayerInfo;
import com.github.derrop.proxy.api.events.connection.service.ServiceConnectionEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInfoAddEvent extends ServiceConnectionEvent {

    private PlayerInfo playerInfo;

    public PlayerInfoAddEvent(@NotNull ServiceConnection connection, PlayerInfo playerInfo) {
        super(connection);
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
