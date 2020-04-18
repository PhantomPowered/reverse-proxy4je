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
package com.github.derrop.proxy.api.task;

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

    @Nullable
    public Throwable getException() {
        return exception;
    }

    @Nullable
    public V getResult() {
        return result;
    }
}
