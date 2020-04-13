package com.github.derrop.proxy.api.command.basic;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class NonTabCompleteableCommandCallback extends BasicCommandCallback {

    public NonTabCompleteableCommandCallback(@NotNull String permission, @Nullable BaseComponent[] help) {
        super(permission, help);
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) {
        return new ArrayList<>();
    }
}
