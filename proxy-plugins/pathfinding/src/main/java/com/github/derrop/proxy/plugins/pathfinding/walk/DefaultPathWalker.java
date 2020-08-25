package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.tick.TickHandler;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.pathfinding.Path;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPathWalker implements PathWalker, TickHandler {

    public static final double TPS = 20; // ticks per second
    public static final double BPT = 2; // blocks per tick TODO use flight/walk speed from the player abilities

    private final Map<UUID, WalkablePath> runningPaths = new ConcurrentHashMap<>();

    @Override
    public void handleTick() {
        for (Map.Entry<UUID, WalkablePath> entry : this.runningPaths.entrySet()) {
            WalkablePath path = entry.getValue();
            if (path.isDone()) {
                path.getFinishHandler().run();

                if (!path.isRecursive()) {
                    this.runningPaths.remove(entry.getKey());
                    continue;
                }
            }

            entry.getValue().handleTick();
        }
    }

    @Override
    public UUID walkPath(ServiceConnection connection, Path path, Runnable roundFinishedHandler) {
        this.testWalking(connection);
        UUID id = UUID.randomUUID();
        this.runningPaths.put(id, new FollowingWalkablePath(connection, path, roundFinishedHandler));
        return id;
    }

    @Override
    public void cancelPath(UUID id) {
        this.runningPaths.remove(id);
    }

    @Override
    public boolean isWalking(UUID id) {
        return this.runningPaths.containsKey(id);
    }

    @Override
    public boolean isWalking(ServiceConnection connection) {
        for (WalkablePath value : this.runningPaths.values()) {
            if (value.getConnection().equals(connection)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public UUID walkDirectPath(ServiceConnection connection, Location location, Runnable finishedHandler) {
        this.testWalking(connection);
        UUID id = UUID.randomUUID();
        this.runningPaths.put(id, new DirectWalkablePath(connection, location, finishedHandler));
        return id;
    }

    private void testWalking(ServiceConnection connection) {
        if (this.isWalking(connection)) {
            throw new IllegalArgumentException("One connection cannot walk two different paths");
        }
    }

}
