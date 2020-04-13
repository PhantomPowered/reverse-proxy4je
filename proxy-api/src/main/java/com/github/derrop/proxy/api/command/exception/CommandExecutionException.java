package com.github.derrop.proxy.api.command.exception;

import com.github.derrop.proxy.api.command.CommandContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandExecutionException extends RuntimeException {

    private static final long serialVersionUID = -4787640698638914653L;

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(@NotNull CommandContainer commandContainer, @Nullable String message) {
        super("Failed to execute command from command container " + commandContainer.getMainAlias() + "! Reason: " + message);
    }
}
