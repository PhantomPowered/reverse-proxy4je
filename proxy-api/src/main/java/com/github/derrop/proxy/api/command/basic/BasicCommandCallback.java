package com.github.derrop.proxy.api.command.basic;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.CommandCallback;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicCommandCallback implements CommandCallback {

    public BasicCommandCallback(@NotNull String permission, @Nullable BaseComponent[] help) {
        this.permission = permission;
        this.help = help == null ? TextComponent.fromLegacyText("§cNo help provided") : help;
    }

    private final String permission;

    private final BaseComponent[] help;

    @Override
    public boolean testPermission(@NotNull CommandSender commandSender) {
        return commandSender.checkPermission(this.permission);
    }

    @Override
    public @NotNull BaseComponent[] getHelp(@NotNull CommandSender commandSender) {
        return this.help;
    }
}
