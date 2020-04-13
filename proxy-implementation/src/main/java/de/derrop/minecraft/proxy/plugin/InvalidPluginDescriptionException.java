package de.derrop.minecraft.proxy.plugin;

public class InvalidPluginDescriptionException extends RuntimeException {

    public InvalidPluginDescriptionException(String message) {
        super(message);
    }

    public InvalidPluginDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
