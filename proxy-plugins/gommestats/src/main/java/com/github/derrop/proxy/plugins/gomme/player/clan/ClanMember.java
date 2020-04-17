package com.github.derrop.proxy.plugins.gomme.player.clan;

import java.util.UUID;

public class ClanMember {

    private UUID uniqueId;
    private String name;
    private Type memberType;

    public ClanMember(UUID uniqueId, String name, Type memberType) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.memberType = memberType;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public Type getMemberType() {
        return memberType;
    }

    public static enum Type {
        ADMIN, MODERATOR, MEMBER
    }

}
