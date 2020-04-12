package de.derrop.minecraft.proxy.api.player;

import java.util.UUID;

public interface OfflinePlayer {

    UUID getUniqueId();

    String getName();

    boolean hasPermission(String permission);

    long getLastLogin();

}
