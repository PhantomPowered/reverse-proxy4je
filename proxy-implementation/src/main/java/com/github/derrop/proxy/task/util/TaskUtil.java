package com.github.derrop.proxy.task.util;

import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.task.DefaultTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class TaskUtil {

    private TaskUtil() {
        throw new UnsupportedOperationException();
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
}
