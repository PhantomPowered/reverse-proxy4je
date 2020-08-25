package com.github.derrop.proxy.plugins.gommecw.running;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ClanWarMember {

    @SerializedName("uuid")
    private final UUID uniqueId;
    private final String name;
    private boolean alive;
    private boolean unknown;

    public ClanWarMember(UUID uniqueId, String name, boolean alive, boolean unknown) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.alive = alive;
        this.unknown = unknown;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isUnknown() {
        return unknown;
    }

    @Override
    public String toString() {
        return "ClanWarMember{"
                + "uniqueId=" + uniqueId
                + ", name='" + name + '\''
                + ", alive=" + alive
                + ", unknown=" + unknown
                + '}';
    }
}
