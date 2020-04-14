package com.github.derrop.proxy.command.console;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.util.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleCommandSender implements CommandSender {

    private static final UUID CONSOLE_UUID = UUID.randomUUID();

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(ChatColor.stripColor(message));
    }

    @Override
    public void sendMessage(@NotNull BaseComponent component) {
        this.sendMessage(ComponentSerializer.toString(component));
    }

    @Override
    public void sendMessage(@NotNull BaseComponent[] component) {
        this.sendMessage(ComponentSerializer.toString(component));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "Console";
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return CONSOLE_UUID;
    }
}
