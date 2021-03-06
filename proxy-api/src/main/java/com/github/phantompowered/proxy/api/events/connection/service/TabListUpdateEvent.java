package com.github.phantompowered.proxy.api.events.connection.service;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.event.Cancelable;
import net.kyori.adventure.text.Component;

public class TabListUpdateEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancelled;

    private Component header;
    private Component footer;

    public TabListUpdateEvent(ServiceConnection serviceConnection, Component header, Component footer) {
        super(serviceConnection);
        this.header = header;
        this.footer = footer;
    }

    public Component getHeader() {
        return this.header;
    }

    public void setHeader(Component header) {
        this.header = header;
    }

    public Component getFooter() {
        return this.footer;
    }

    public void setFooter(Component footer) {
        this.footer = footer;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
