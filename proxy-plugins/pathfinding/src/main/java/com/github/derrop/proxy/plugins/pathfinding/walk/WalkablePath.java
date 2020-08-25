package com.github.derrop.proxy.plugins.pathfinding.walk;

import com.github.derrop.proxy.api.connection.ServiceConnection;

public abstract class WalkablePath {

    private final ServiceConnection connection;
    private final Runnable finishHandler;

    public WalkablePath(ServiceConnection connection, Runnable finishHandler) {
        this.connection = connection;
        this.finishHandler = finishHandler;
    }

    public Runnable getFinishHandler() {
        return finishHandler;
    }

    public ServiceConnection getConnection() {
        return connection;
    }

    public abstract boolean isRecursive();

    public abstract boolean isDone();

    public abstract void handleTick();

}
