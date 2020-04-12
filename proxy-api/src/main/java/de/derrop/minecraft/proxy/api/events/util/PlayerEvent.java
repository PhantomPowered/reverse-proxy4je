package de.derrop.minecraft.proxy.api.events.util;

import de.derrop.minecraft.proxy.api.connection.ProtocolDirection;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEvent extends Event {

    public PlayerEvent(@Nullable ProxiedPlayer player, @NotNull ProtocolDirection direction) {
        this.player = player;
        this.direction = direction;
    }

    private final ProxiedPlayer player;
    private final ProtocolDirection direction;

    @Nullable
    public ProxiedPlayer getPlayer() {
        return player;
    }

    @NotNull
    public ProtocolDirection getDirection() {
        return direction;
    }
}
