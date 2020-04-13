package de.derrop.minecraft.proxy.api.events.connection;

import de.derrop.minecraft.proxy.api.connection.Connection;
import de.derrop.minecraft.proxy.api.event.Event;
import org.jetbrains.annotations.NotNull;

public class ConnectionEvent extends Event {

    private final Connection connection;

    public ConnectionEvent(@NotNull Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public Connection getConnection() {
        return this.connection;
    }
}
