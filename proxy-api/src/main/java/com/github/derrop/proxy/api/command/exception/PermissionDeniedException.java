package com.github.derrop.proxy.api.command.exception;

import com.github.derrop.proxy.api.command.CommandContainer;
import org.jetbrains.annotations.NotNull;

public class PermissionDeniedException extends RuntimeException {

    private static final long serialVersionUID = 6141362853487083200L;

    public PermissionDeniedException(@NotNull CommandContainer commandContainer) {
        super("You do not have permission to execute " + commandContainer.getMainAlias());
    }
}
