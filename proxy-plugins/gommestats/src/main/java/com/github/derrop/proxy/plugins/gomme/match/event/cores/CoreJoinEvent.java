package com.github.derrop.proxy.plugins.gomme.match.event.cores;

import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.gomme.match.event.MatchEvent;

// TODO add the same for BedWars with bed instead of beacon
public class CoreJoinEvent extends MatchEvent {

    private final String player;
    private final Location core;

    public CoreJoinEvent(String player, Location core) {
        this.player = player;
        this.core = core;
    }

    public String getPlayer() {
        return this.player;
    }

    public Location getCore() {
        return this.core;
    }
}
