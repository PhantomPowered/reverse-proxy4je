package com.github.derrop.proxy.command;

import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.logging.ILogger;
import com.github.derrop.proxy.api.util.ChatColor;

import java.util.UUID;

public class ConsoleCommandSender implements CommandSender {

    private static final UUID CONSOLE_UUID = UUID.randomUUID();

    private ILogger logger;

    public ConsoleCommandSender(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void sendMessage(String message) {
        this.logger.info(ChatColor.stripColor(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public UUID getUniqueId() {
        return CONSOLE_UUID;
    }
}
