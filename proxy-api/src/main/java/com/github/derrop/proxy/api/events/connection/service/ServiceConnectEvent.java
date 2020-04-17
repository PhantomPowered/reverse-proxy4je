package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class ServiceConnectEvent extends ServiceConnectionEvent implements Cancelable {

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
