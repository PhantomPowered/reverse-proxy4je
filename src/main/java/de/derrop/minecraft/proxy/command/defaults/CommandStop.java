package de.derrop.minecraft.proxy.command.defaults;

import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.command.ConsoleCommandSender;

public class CommandStop extends Command {

    public CommandStop() {
        super("stop", "exit");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command is only available for the console");
            return;
        }

        sender.sendMessage("Shutting down...");
        MCProxy.getInstance().shutdown();
    }
}
