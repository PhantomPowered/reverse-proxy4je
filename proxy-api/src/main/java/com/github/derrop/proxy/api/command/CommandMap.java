package com.github.derrop.proxy.api.command;

import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.CommandRegistrationException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandMap {

    @NotNull
    default CommandContainer registerCommand(@Nullable Plugin plugin, @NotNull CommandCallback commandCallback, @NotNull String... aliases) throws CommandRegistrationException {
        return this.registerCommand(plugin, commandCallback, Arrays.asList(aliases));
    }

    @NotNull
    CommandContainer registerCommand(@Nullable Plugin plugin, @NotNull CommandCallback commandCallback, @NotNull List<String> aliases) throws CommandRegistrationException;

    @NotNull
    Optional<CommandContainer> getCommandContainer(@NotNull String anyName);

    @NotNull
    Collection<CommandContainer> getCommandsByPlugin(@NotNull Plugin plugin);

    @NotNull
    Collection<CommandContainer> getAllCommands();

    @NotNull
    CommandResult process(@NotNull CommandSender commandSender, @NotNull String fullLine) throws CommandExecutionException, PermissionDeniedException;

    @NotNull
    List<String> getSuggestions(@NotNull CommandSender commandSender, @NotNull String fullLine);

    int getSize();
}
