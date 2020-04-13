package de.derrop.minecraft.proxy.api.events.connection.player;

import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerLogoutEvent extends PlayerEvent { // TODO
    public PlayerLogoutEvent(@NotNull ProxiedPlayer player) {
        super(player);
    }
}
