package com.github.derrop.proxy.plugins.gomme.player.clan;

import com.github.derrop.proxy.plugins.gomme.player.Tag;

import java.util.Collection;

public class ClanInfo {

    private String name;
    private String shortcut;
    private Collection<Tag> tags;
    private ClanMember[] members;

    public ClanInfo(String name, String shortcut, Collection<Tag> tags, ClanMember[] members) {
        this.name = name;
        this.shortcut = shortcut;
        this.tags = tags;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public String getShortcut() {
        return shortcut;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public ClanMember[] getMembers() {
        return members;
    }
}
