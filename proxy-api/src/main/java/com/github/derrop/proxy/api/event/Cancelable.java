package com.github.derrop.proxy.api.event;

public interface Cancelable {

    /**
     * Marks the current event as (not) cancelled
     *
     * @param cancel If the event should get cancelled
     */
    void cancel(boolean cancel);

    /**
     * @return If the current event is cancelled
     */
    boolean isCancelled();
}
