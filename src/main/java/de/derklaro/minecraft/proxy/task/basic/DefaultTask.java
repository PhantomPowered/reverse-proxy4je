package de.derklaro.minecraft.proxy.task.basic;

import de.derklaro.minecraft.proxy.task.Task;
import de.derklaro.minecraft.proxy.task.TaskFutureListener;
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
