package com.github.derrop.proxy.plugins.gomme.clan.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClanId {

    private final Type type;
    private final String id;
    private final String requestMessage;

    public ClanId(@NotNull Type type, @NotNull String id, @NotNull String requestMessage) {
        this.type = type;
        this.id = id;
        this.requestMessage = requestMessage;
    }

    public Type getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getRequestMessage() {
        return this.requestMessage;
    }

    @NotNull
    public static ClanId forTag(@NotNull String tag) {
        return new ClanId(Type.TAG, tag, "/clan tinfo " + tag);
    }

    @NotNull
    public static ClanId forUser(@NotNull String userName) {
        return new ClanId(Type.USER, userName, "/clan uinfo " + userName);
    }

    @NotNull
    public static ClanId forName(@NotNull String clanName) {
        return new ClanId(Type.NAME, clanName, "/clan ninfo " + clanName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanId clanId = (ClanId) o;
        return type == clanId.type &&
                id.equalsIgnoreCase(clanId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    public enum Type {
        USER, TAG, NAME
    }

}
