package com.github.phantompowered.proxy.api.connection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface Whitelist {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isWhitelisted(@NotNull UUID uniqueId);

    void addEntry(@NotNull UUID uniqueId);

    void removeEntry(@NotNull UUID uniqueId);

    @NotNull
    Collection<UUID> getEntries();

    void clear();

    long size();

}
