package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.events.connection.ConnectionEvent;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import org.jetbrains.annotations.NotNull;

public class ServiceConnectionEvent extends ConnectionEvent {

    private final ServiceConnection connection;

    public ServiceConnectionEvent(@NotNull ServiceConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

}
