package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.api.player.Side;

import java.util.function.Supplier;

public class AppendedActionBar {

    private final Side side;
    private final Supplier<String> message;

    public AppendedActionBar(Side side, Supplier<String> message) {
        this.side = side;
        this.message = message;
    }

    public Side getSide() {
        return this.side;
    }

    public Supplier<String> getMessage() {
        return this.message;
    }
}
