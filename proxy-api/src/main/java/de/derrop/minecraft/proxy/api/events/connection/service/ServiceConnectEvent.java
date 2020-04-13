package de.derrop.minecraft.proxy.api.events.connection.service;

import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class ServiceConnectEvent extends ServiceConnectionEvent implements Cancelable { // TODO

    private boolean cancel;

    public ServiceConnectEvent(@NotNull ServiceConnection connection) {
        super(connection);
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
}
