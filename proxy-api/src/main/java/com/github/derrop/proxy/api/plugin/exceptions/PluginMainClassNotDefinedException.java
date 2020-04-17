package com.github.derrop.proxy.api.plugin.exceptions;

public class PluginMainClassNotDefinedException extends RuntimeException {

    private static final long serialVersionUID = -3165896031810521631L;

    public PluginMainClassNotDefinedException(String message) {
        super(message);
    }
}
