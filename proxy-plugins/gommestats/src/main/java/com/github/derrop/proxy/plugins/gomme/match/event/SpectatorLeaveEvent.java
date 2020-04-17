package com.github.derrop.proxy.plugins.gomme.match.event;

public class SpectatorLeaveEvent extends MatchEvent {

    private final String name;

    public SpectatorLeaveEvent(String name) {
        super(Type.SPECTATOR_LEFT);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
