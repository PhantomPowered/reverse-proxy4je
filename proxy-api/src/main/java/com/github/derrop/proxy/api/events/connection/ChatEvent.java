package com.github.derrop.proxy.api.events.connection;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class ChatEvent extends ConnectionEvent implements Cancelable {

    private boolean cancel;
    private final ProtocolDirection direction;

    private BaseComponent[] message;

    public ChatEvent(@NotNull Connection connection, @NotNull ProtocolDirection direction, @NotNull BaseComponent[] message) {
        super(connection);
        this.direction = direction;
        this.message = message;
    }

    @NotNull
    public ProtocolDirection getDirection() {
        return this.direction;
    }

    @NotNull
    public BaseComponent[] getMessage() {
        return this.message;
    }

    public void setMessage(BaseComponent[] message) {
        this.message = message;
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
