package de.derrop.minecraft.proxy.exception;

import java.net.ConnectException;

public class KickedException extends ConnectException {
    public KickedException(String message) {
        super(message);
    }
}
