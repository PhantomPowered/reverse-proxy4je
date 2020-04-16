package com.github.derrop.proxy.api.network.exception;

public class ComponentTooLargeException extends RuntimeException {

    private static final long serialVersionUID = -3493345182483647931L;

    public ComponentTooLargeException(String message) {
        super(message);
    }
}
