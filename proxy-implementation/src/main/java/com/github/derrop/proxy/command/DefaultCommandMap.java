/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.command;

import com.github.derrop.proxy.api.command.CommandCallback;
import com.github.derrop.proxy.api.command.CommandContainer;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.CommandRegistrationException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.exception.UnknownCommandException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultCommandMap implements CommandMap {

    private final Map<String, CommandContainer> commands = new ConcurrentHashMap<>();

    @Override
    public @NotNull CommandContainer registerCommand(@Nullable PluginContainer pluginContainer, @NotNull CommandCallback commandCallback, @NotNull List<String> aliases) throws CommandRegistrationException {
        if (aliases.isEmpty()) {
            throw new CommandRegistrationException("Unable to register command which has no aliases provided");
        }

        String mainAlias = aliases.get(0);
        CommandContainer commandContainer = new DefaultCommandContainer(pluginContainer, commandCallback, mainAlias, new HashSet<>(aliases.subList(1, aliases.size())));

        for (String alias : aliases) {
            this.commands.put(alias.toLowerCase(), commandContainer);
        }

        return commandContainer;
    }

    @Override
    public @NotNull Optional<CommandContainer> getCommandContainer(@NotNull String anyName) {
        return Optional.ofNullable(this.commands.get(anyName.toLowerCase()));
    }

    @Override
    public @NotNull Collection<CommandContainer> getCommandsByPlugin(@NotNull PluginContainer pluginContainer) {
        Collection<CommandContainer> out = new ArrayList<>();
        for (CommandContainer value : this.commands.values()) {
            if (value.getPluginContainer() != null && value.getPluginContainer().equals(pluginContainer)) {
                out.add(value);
            }
        }

        return out;
    }

    @Override
    public @NotNull Collection<CommandContainer> getAllCommands() {
        return Collections.unmodifiableCollection(this.commands.values());
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String fullLine) throws CommandExecutionException, PermissionDeniedException {
        String[] split = fullLine.split(" ");
        try {
            CommandContainer commandContainer = this.getContainer(commandSender, split);
            if (!commandContainer.getCallback().testPermission(commandSender)) {
                commandSender.sendMessage("You do not have permission to execute this command");
                return CommandResult.FAILURE;
            }

            String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
            return commandContainer.getCallback().process(commandSender, args, fullLine);
        } catch (final UnknownCommandException ex) {
            return CommandResult.NOT_FOUND;
        }
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSender commandSender, @NotNull String fullLine) {
        String[] split = fullLine.split(" ");
        try {
            CommandContainer commandContainer = this.getContainer(commandSender, split);
            String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
            return commandContainer.getCallback().getSuggestions(commandSender, args, fullLine);
        } catch (UnknownCommandException ex) {
            return this.commands.entrySet().stream()
                    .filter(entry -> entry.getValue().getCallback().testPermission(commandSender))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (final CommandExecutionException | PermissionDeniedException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public int getSize() {
        return this.commands.size();
    }

    @NotNull
    private CommandContainer getContainer(@NotNull CommandSender commandSender, @NotNull String[] split) throws CommandExecutionException, UnknownCommandException, PermissionDeniedException {
        if (split.length == 0) {
            throw new CommandExecutionException("Unable to process command with no arguments or command given");
        }

        CommandContainer commandContainer = this.commands.get(split[0].toLowerCase());
        if (commandContainer == null) {
            throw new UnknownCommandException("Unable to find command by name " + split[0]);
        }

        if (!commandContainer.getCallback().testPermission(commandSender)) {
            throw new PermissionDeniedException(commandContainer);
        }

        return commandContainer;
    }
}
