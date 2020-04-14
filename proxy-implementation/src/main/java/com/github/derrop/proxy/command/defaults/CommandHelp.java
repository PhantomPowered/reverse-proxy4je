package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.command.CommandContainer;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
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
