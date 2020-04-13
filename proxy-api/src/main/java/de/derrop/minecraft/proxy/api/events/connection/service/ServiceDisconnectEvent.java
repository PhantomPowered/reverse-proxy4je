package de.derrop.minecraft.proxy.api.events.connection.service;

import de.derrop.minecraft.proxy.api.chat.component.BaseComponent;
import de.derrop.minecraft.proxy.api.connection.ServiceConnection;
import de.derrop.minecraft.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServiceDisconnectEvent extends ServiceConnectionEvent implements Cancelable { // TODO

    private boolean cancel;
    private BaseComponent[] reason;

    public ServiceDisconnectEvent(@NotNull ServiceConnection connection, @Nullable BaseComponent[] reason) {
        super(connection);
        this.reason = reason;
    }

    @Nullable
    public BaseComponent[] getReason() {
        return this.reason;
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
