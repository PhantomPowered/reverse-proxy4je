package com.github.derrop.proxy.exception;

import java.net.ConnectException;

public class KickedException extends ConnectException {
    public KickedException(String message) {
        super(message);
    }
}
