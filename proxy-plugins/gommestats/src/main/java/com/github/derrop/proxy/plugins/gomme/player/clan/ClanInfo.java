package com.github.derrop.proxy.plugins.gomme.player.clan;

public class ClanInfo {

    private String name;
    private String shortcut;
    private ClanMember[] members;

    public ClanInfo(String name, String shortcut, ClanMember[] members) {
        this.name = name;
        this.shortcut = shortcut;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public String getShortcut() {
        return shortcut;
    }

    public ClanMember[] getMembers() {
        return members;
    }
}
