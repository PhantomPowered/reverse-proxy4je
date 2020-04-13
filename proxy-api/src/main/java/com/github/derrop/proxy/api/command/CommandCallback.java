package com.github.derrop.proxy.api.command;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandCallback {

    @NotNull
    CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException;

    @NotNull List<String> getSuggestions(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine);

    boolean testPermission(@NotNull CommandSender commandSender);

    @NotNull BaseComponent[] getHelp(@NotNull CommandSender commandSender);
}
