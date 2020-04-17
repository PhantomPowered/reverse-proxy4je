package com.github.derrop.proxy.plugins.gomme.match.event;

public class SpectatorJoinEvent extends MatchEvent {

    private final String name;

    public SpectatorJoinEvent(String name) {
        super(Type.SPECTATOR_JOINED);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
