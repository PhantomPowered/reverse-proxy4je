package com.github.derrop.proxy.api.events.connection;

import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class PluginMessageEvent extends ConnectionEvent implements Cancelable {

    private boolean cancel = false;

    private final ProtocolDirection direction;

    private String tag;
    private byte[] data;

    public PluginMessageEvent(@NotNull Connection connection, ProtocolDirection direction, @NotNull String tag, @NotNull byte[] data) {
        super(connection);
        this.direction = direction;
        this.tag = tag;
        this.data = data;
    }

    @NotNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    @NotNull
    public byte[] getData() {
        return data;
    }

    public void setData(@NotNull byte[] data) {
        this.data = data;
    }

    @NotNull
    public ProtocolDirection getDirection() {
        return this.direction;
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
