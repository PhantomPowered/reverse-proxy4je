package com.github.derrop.proxy.plugins.gomme.match;

import com.github.derrop.proxy.api.chat.ChatColor;

import java.util.Collection;
import java.util.UUID;

public class MatchTeam {

    private String name;
    private ChatColor color;
    private Collection<UUID> players;

    public MatchTeam(String name, ChatColor color, Collection<UUID> players) {
        this.name = name;
        this.color = color;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Collection<UUID> getPlayers() {
        return players;
    }
}
