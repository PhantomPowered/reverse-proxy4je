package com.github.derrop.proxy.api.events.connection;

import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.event.Event;
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
