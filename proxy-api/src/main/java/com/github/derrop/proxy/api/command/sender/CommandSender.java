package com.github.derrop.proxy.api.command.sender;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CommandSender {

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull BaseComponent component);

    void sendMessage(@NotNull BaseComponent[] component);

    void sendMessages(@NotNull String... messages);

    boolean hasPermission(@NotNull String permission);

    @NotNull
    String getName();

    @NotNull
    UUID getUniqueId();

}
