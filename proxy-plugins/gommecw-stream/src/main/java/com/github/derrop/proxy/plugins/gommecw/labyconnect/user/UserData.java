package com.github.derrop.proxy.plugins.gommecw.labyconnect.user;

import java.util.UUID;

public class UserData {

    private final UUID uniqueId;
    private final byte rank;

    public UserData(UUID uniqueId, byte rank) {
        this.uniqueId = uniqueId;
        this.rank = rank;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public byte getRank() {
        return rank;
    }
}
