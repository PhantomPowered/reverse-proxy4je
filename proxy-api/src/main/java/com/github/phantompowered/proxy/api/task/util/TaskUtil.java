/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.api.task.util;

import com.github.phantompowered.proxy.api.task.DefaultTask;
import com.github.phantompowered.proxy.api.task.Task;
import com.github.phantompowered.proxy.api.task.TaskFutureListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public final class TaskUtil {

    private TaskUtil() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static <V> Task<V> completedTask(@NotNull V result) {
        return completedTask(result, Collections.emptyList());
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
