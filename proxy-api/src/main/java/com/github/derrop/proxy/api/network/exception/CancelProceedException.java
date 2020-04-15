package com.github.derrop.proxy.api.network.exception;

public class CancelProceedException extends RuntimeException {
    private CancelProceedException() {
    }
    private CancelProceedException(String message) {
        super(message);
    }
    private CancelProceedException(String message, Throwable cause) {
        super(message, cause);
    }
    private CancelProceedException(Throwable cause) {
        super(cause);
    }
    private CancelProceedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static final CancelProceedException INSTANCE = new CancelProceedException();
    private static final long serialVersionUID = -7091455510942501654L;

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
