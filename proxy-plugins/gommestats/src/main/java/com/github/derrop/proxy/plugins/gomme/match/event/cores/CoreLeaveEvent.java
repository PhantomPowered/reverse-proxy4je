package com.github.derrop.proxy.plugins.gomme.match.event.cores;

import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;

public class CoreLeaveEvent extends MatchEvent {

    private final PlayerInfo player;
    private final Location core;

    public CoreLeaveEvent(PlayerInfo player, Location core) {
        this.player = player;
        this.core = core;
    }

    public PlayerInfo getPlayer() {
        return this.player;
    }

    public Location getCore() {
        return this.core;
    }

    @Override
    public String toPlainText() {
        return "Player " + this.player.getUsername() + " is no more near the core at " + this.core.toShortString();
    }
}
