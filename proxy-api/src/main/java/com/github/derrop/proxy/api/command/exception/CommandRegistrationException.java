package com.github.derrop.proxy.api.command.exception;

import org.jetbrains.annotations.NotNull;

public class CommandRegistrationException extends RuntimeException {

    private static final long serialVersionUID = -4972822908761790913L;

    public CommandRegistrationException(@NotNull String message) {
        super(message);
    }
}
