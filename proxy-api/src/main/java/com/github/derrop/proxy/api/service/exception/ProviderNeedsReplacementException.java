package com.github.derrop.proxy.api.service.exception;

import org.jetbrains.annotations.NotNull;

public class ProviderNeedsReplacementException extends RuntimeException {

    private static final long serialVersionUID = 341293615651430505L;

    public ProviderNeedsReplacementException(@NotNull Class<?> service) {
        super("The service " + service.getName() + " needs a replacement");
    }
}
