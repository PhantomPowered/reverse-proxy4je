package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.command.ConsoleCommandSender;

public class CommandStop extends Command {

    public CommandStop() {
        super("stop", "exit");
        super.setPermission("command.stop");
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
