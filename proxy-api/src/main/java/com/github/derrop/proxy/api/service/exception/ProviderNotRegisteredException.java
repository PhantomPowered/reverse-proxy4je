package com.github.derrop.proxy.api.service.exception;

import org.jetbrains.annotations.NotNull;

public class ProviderNotRegisteredException extends RuntimeException {

    private static final long serialVersionUID = -7221445052053213876L;

    public ProviderNotRegisteredException(@NotNull Class<?> service) {
        super("No provider for service " + service.getName() + " is registered");
    }
}
