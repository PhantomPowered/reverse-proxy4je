package com.github.derrop.proxy.plugins.gommecw.labyconnect.user;

public enum UserStatus {

    ONLINE((byte) 0, "a"),
    AWAY((byte) 1, "b"),
    BUSY((byte) 2, "5"),
    OFFLINE((byte) -1, "c"),
    ;
    private final byte id;
    private final String chatColor;
    private final String name;

    UserStatus(byte id, String chatColor) {
        this.id = id;
        this.chatColor = chatColor;
        this.name = ("user_status_" + name().toLowerCase());
    }

    public static UserStatus getById(int id) {
        for (UserStatus status : values()) {
            if (status.id == id)
                return status;
        }
        return OFFLINE;
    }

    public byte getId() {
        return this.id;
    }

    public String getChatColor() {
        return this.chatColor;
    }

    public String getName() {
        return this.name;
    }
}


