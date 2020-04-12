package de.derklaro.minecraft.proxy.events.util;

import de.derklaro.minecraft.proxy.event.Event;
import net.md_5.bungee.connection.Connection;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConnectionEvent extends Event {

    public ConnectionEvent(@Nullable Connection connection, @NotNull ProtocolConstants.Direction direction) {
        this.connection = connection;
        this.direction = direction;
    }

    private final Connection connection;

    private final ProtocolConstants.Direction direction;

    @Nullable
    public Connection getConnection() {
        return connection;
    }

    @NotNull
    public ProtocolConstants.Direction getDirection() {
        return direction;
    }
}
