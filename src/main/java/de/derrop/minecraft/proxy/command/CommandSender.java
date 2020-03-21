package de.derrop.minecraft.proxy.command;

import java.util.UUID;

public interface CommandSender {

    void sendMessage(String message);

    void sendMessages(String... messages);

    boolean hasPermission(String permission);

    String getName();

    UUID getUniqueId();

}
