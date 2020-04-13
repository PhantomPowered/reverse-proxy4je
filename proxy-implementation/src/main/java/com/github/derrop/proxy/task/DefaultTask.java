package com.github.derrop.proxy.task;

import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultTask<V> extends Task<V> {

    private final Collection<TaskFutureListener<V>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public @NotNull Task<V> addListener(@NotNull TaskFutureListener<V> listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public @NotNull Collection<TaskFutureListener<V>> getListeners() {
        return this.listeners;
    }
}
