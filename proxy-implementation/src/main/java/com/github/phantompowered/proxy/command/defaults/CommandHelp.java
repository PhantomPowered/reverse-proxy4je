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
package com.github.phantompowered.proxy.command.defaults;

import com.github.phantompowered.proxy.api.command.CommandContainer;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandHelp extends NonTabCompleteableCommandCallback {

    private final CommandMap commandMap;

    public CommandHelp(@NotNull CommandMap commandMap) {
        super("proxy.command.help", null);
        this.commandMap = commandMap;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        commandSender.sendMessage("Available commands:");
        for (String command : this.getAllCommands(commandSender)) {
            commandSender.sendMessage(command);
        }

        return CommandResult.END;
    }

    @NotNull
    private Collection<String> getAllCommands(@NotNull CommandSender commandSender) {
        Collection<CommandContainer> out = new ArrayList<>();
        for (CommandContainer allCommand : this.commandMap.getAllCommands()) {
            if (allCommand.getCallback() == this || !allCommand.getCallback().testPermission(commandSender)
                    || out.stream().anyMatch(e -> e.getMainAlias().equals(allCommand.getMainAlias()))) {
                continue;
            }

            out.add(allCommand);
        }

        return out.stream().map(e -> " - " + e.getMainAlias()).collect(Collectors.toList());
    }
}
