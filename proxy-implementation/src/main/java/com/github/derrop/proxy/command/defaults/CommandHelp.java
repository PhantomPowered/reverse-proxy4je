package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.CommandSender;

public class CommandHelp extends Command {

    private CommandMap commandMap;

    public CommandHelp(CommandMap commandMap) {
        super("help", "?");
        this.commandMap = commandMap;
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        sender.sendMessage("Available commands:");
        for (Command command : this.commandMap.getCommands()) {
            if (command != this && command.canExecute(sender)) {
                sender.sendMessage("- " + command.getNames()[0] + " (Permission: " + command.getPermission() + ")");
            }
        }
    }
}
