package com.github.derrop.proxy.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class UnknownCommandException extends RuntimeException {

    private static final long serialVersionUID = -1768064659716742483L;

    public UnknownCommandException(@NotNull String message) {
        super(message);
    }
}
