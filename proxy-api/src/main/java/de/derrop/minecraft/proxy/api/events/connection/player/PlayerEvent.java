package de.derrop.minecraft.proxy.api.events.connection.player;

import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;
import de.derrop.minecraft.proxy.api.event.Event;
import de.derrop.minecraft.proxy.api.events.connection.ConnectionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEvent extends ConnectionEvent {

    public PlayerEvent(@NotNull ProxiedPlayer player) {
        super(player);
        this.player = player;
    }

    private final ProxiedPlayer player;

    @Nullable
    public ProxiedPlayer getPlayer() {
        return player;
    }

}
