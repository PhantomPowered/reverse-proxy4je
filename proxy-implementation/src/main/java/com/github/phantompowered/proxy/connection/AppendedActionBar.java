package com.github.phantompowered.proxy.connection;

import com.github.phantompowered.proxy.api.block.half.HorizontalHalf;

import java.util.function.Supplier;

public class AppendedActionBar {

    private final HorizontalHalf side;
    private final Supplier<String> message;

    public AppendedActionBar(HorizontalHalf side, Supplier<String> message) {
        this.side = side;
        this.message = message;
    }

    public HorizontalHalf getSide() {
        return this.side;
    }

    public Supplier<String> getMessage() {
        return this.message;
    }
}
