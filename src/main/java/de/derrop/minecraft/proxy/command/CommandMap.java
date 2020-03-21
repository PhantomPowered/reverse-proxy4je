package de.derrop.minecraft.proxy.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {

    private static final String PREFIX = "/p ";

    private Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        for (String name : command.getNames()) {
            this.commands.put(name.toLowerCase(), command);
        }
    }

    public boolean dispatchCommand(CommandSender sender, String line) {
        if (!line.startsWith(PREFIX)) {
            return false;
        }

        line = line.substring(PREFIX.length());

        String[] args = line.split(" ");
        Command command = this.commands.get(args[0].toLowerCase());
        if (command == null) {
            return true;
        }
        if (!command.canExecute(sender)) {
            sender.sendMessage("You don't have the permission to execute this command");
            return true;
        }

        command.execute(sender, line, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

}
