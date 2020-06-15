package com.github.derrop.proxy.api.events.connection.service.entity;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public class EntityMoveEvent extends Event {

    private final ServiceConnection connection;
    private final Entity entity;

    private final Location from;
    private final Location to;

    public EntityMoveEvent(@NotNull ServiceConnection connection, @NotNull Entity entity, @NotNull Location from, @NotNull Location to) {
        this.connection = connection;
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    @NotNull
    public ServiceConnection getConnection() {
        return this.connection;
    }

    @NotNull
    public Entity getEntity() {
        return this.entity;
    }

    @NotNull
    public Location getFrom() {
        return this.from;
    }

    @NotNull
    public Location getTo() {
        return this.to;
    }

}
