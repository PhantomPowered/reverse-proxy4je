package com.github.derrop.proxy.api.service.exception;

import org.jetbrains.annotations.NotNull;

public class ProviderImmutableException extends RuntimeException {

    private static final long serialVersionUID = -1746245369715004075L;

    public ProviderImmutableException(@NotNull Class<?> service) {
        super("The provider of service " + service.getName() + " is immutable");
    }
}
