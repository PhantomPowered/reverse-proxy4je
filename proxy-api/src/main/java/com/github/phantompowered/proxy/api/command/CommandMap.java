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
package com.github.phantompowered.proxy.api.command;

import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.exception.CommandRegistrationException;
import com.github.phantompowered.proxy.api.command.exception.PermissionDeniedException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandMap {

    @NotNull
    default CommandContainer registerCommand(@Nullable PluginContainer pluginContainer, @NotNull CommandCallback commandCallback, @NotNull String... aliases) throws CommandRegistrationException {
        return this.registerCommand(pluginContainer, commandCallback, Arrays.asList(aliases));
    }

    @NotNull
    CommandContainer registerCommand(@Nullable PluginContainer pluginContainer, @NotNull CommandCallback commandCallback, @NotNull List<String> aliases) throws CommandRegistrationException;

    @NotNull
    Optional<CommandContainer> getCommandContainer(@NotNull String anyName);

    @NotNull
    Collection<CommandContainer> getCommandsByPlugin(@NotNull PluginContainer pluginContainer);

    @NotNull
    Collection<CommandContainer> getAllCommands();

    @NotNull
    CommandResult process(@NotNull CommandSender commandSender, @NotNull String fullLine) throws CommandExecutionException, PermissionDeniedException;

    @NotNull
    List<String> getSuggestions(@NotNull CommandSender commandSender, @NotNull String fullLine);

    int getSize();
}
