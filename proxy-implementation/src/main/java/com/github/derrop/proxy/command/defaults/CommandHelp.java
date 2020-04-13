package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.command.CommandContainer;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandHelp extends NonTabCompleteableCommandCallback {

    private final CommandMap commandMap;

    public CommandHelp(@NotNull CommandMap commandMap) {
        super("proxy.command.help", null);
        this.commandMap = commandMap;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        commandSender.sendMessage("Available commands:");
        for (CommandContainer command : this.commandMap.getAllCommands()) {
            if (command.getCallback() != this && command.getCallback().testPermission(commandSender)) {
                commandSender.sendMessage(" - " + command.getMainAlias());
            }
        }

        return CommandResult.END;
    }
}
