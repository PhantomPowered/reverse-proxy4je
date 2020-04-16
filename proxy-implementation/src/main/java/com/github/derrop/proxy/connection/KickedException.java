package com.github.derrop.proxy.connection;

import java.net.ConnectException;

public class KickedException extends ConnectException {
    public KickedException(String message) {
        super(message);
    }
}
