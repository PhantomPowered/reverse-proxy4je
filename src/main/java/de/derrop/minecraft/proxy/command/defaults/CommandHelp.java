package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.CommandSender;

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
