package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.pathfinding.Path;

import java.util.UUID;

public interface PathWalker {

    // the handler can be called multiple times when the path is recursive
    UUID walkPath(ServiceConnection connection, Path path, Runnable roundFinishedHandler);

    void cancelPath(UUID id);

    boolean isWalking(UUID id);

    boolean isWalking(ServiceConnection connection);

    UUID walkDirectPath(ServiceConnection connection, Location location, Runnable finishedHandler);

}
