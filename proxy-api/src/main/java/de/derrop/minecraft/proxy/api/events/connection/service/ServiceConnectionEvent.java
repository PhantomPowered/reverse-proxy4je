package de.derrop.minecraft.proxy.api.events.connection.service;

import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.events.connection.ConnectionEvent;
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
