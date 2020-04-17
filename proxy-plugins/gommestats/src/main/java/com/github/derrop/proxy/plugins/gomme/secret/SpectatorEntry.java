package com.github.derrop.proxy.plugins.gomme.secret;

public class SpectatorEntry {

    private final String player;
    private final String team;
    private long lastRemove = -1;

    public SpectatorEntry(String player, String team) {
        this.player = player;
        this.team = team;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getTeam() {
        return this.team;
    }

    public void setLastRemove() {
        this.lastRemove = System.currentTimeMillis();
    }

    public boolean isLeft() {
        return this.lastRemove != -1 && this.lastRemove + 500 > System.currentTimeMillis();
    }

}
