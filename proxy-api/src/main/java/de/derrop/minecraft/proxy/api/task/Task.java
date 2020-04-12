package de.derrop.minecraft.proxy.api.task;

import de.derrop.minecraft.proxy.api.task.basic.DefaultTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Task<V> extends CompletableFuture<V> {

    private V result;

    private Throwable exception;

    @NotNull
    public abstract Task<V> addListener(@NotNull TaskFutureListener<V> listener);

    @NotNull
    public abstract Collection<TaskFutureListener<V>> getListeners();

    @NotNull
    @SafeVarargs
    public final Task<V> addListeners(@NotNull TaskFutureListener<V>... listeners) {
        for (TaskFutureListener<V> listener : listeners) {
            this.addListener(listener);
        }

        return this;
    }

    @Nullable
    public V getUninterruptedly() {
        try {
            this.get();
        } catch (final InterruptedException | ExecutionException ex) {
            this.completeExceptionally(ex);
        }

        return this.result;
    }

    @Nullable
    public V getUninterruptedly(long time, TimeUnit timeUnit) {
        try {
            this.get(time, timeUnit);
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            this.completeExceptionally(ex);
        }

        return this.result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = super.cancel(mayInterruptIfRunning);
        this.getListeners().forEach(e -> e.onCancel(this));
        return result;
    }

    @Override
    public boolean complete(V value) {
        boolean result = super.complete(value);

        this.result = value;
        this.getListeners().forEach(e -> e.onSuccess(this));

        return result;
    }

    @Override
    public boolean completeExceptionally(Throwable ex) {
        boolean result = super.completeExceptionally(ex);

        this.exception = ex;
        this.getListeners().forEach(e -> e.onFailure(this));

        return result;
    }

    @NotNull
    public static <V> Task<V> completedTask(@NotNull V result, @NotNull Collection<TaskFutureListener<V>> listeners) {
        Task<V> task = new DefaultTask<>();
        for (TaskFutureListener<V> listener : listeners) {
            task.addListener(listener);
        }

        task.complete(result);
        return task;
    }

    @Nullable
    public Throwable getException() {
        return exception;
    }

    @Nullable
    public V getResult() {
        return result;
    }
}
