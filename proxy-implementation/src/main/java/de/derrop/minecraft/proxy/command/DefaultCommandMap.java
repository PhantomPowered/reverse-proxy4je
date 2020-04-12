package de.derrop.minecraft.proxy.command;

import de.derrop.minecraft.proxy.api.command.Command;
import de.derrop.minecraft.proxy.api.command.CommandMap;
import de.derrop.minecraft.proxy.api.command.CommandSender;
import de.derrop.minecraft.proxy.logging.ILogger;
import net.md_5.bungee.Util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultCommandMap implements CommandMap {

    public static final String PREFIX = "/proxy ";

    private final CommandSender console;

    private final Map<String, Command> commands = new HashMap<>();

    public DefaultCommandMap(ILogger logger) {
        this.console = new ConsoleCommandSender(logger);
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public void registerCommand(Command command) {
        for (String name : command.getNames()) {
            this.commands.put(name.toLowerCase(), command);
        }
    }

    @Override
    public Collection<Command> getCommands() {
        return this.commands.values().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean dispatchCommand(String line) {
        return this.dispatchCommand(this.console, line);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String line) {
        if (!line.startsWith(PREFIX)) {
            return false;
        }

        line = line.substring(PREFIX.length());

        String[] args = line.split(" ");
        Command command = this.commands.get(args[0].toLowerCase());
        if (command == null) {
            sender.sendMessage("§cCommand not found");
            return true;
        }
        if (!command.canExecute(sender)) {
            sender.sendMessage("You don't have the permission to execute this command");
            return true;
        }

        try {
            command.execute(sender, line, Arrays.copyOfRange(args, 1, args.length));
        } catch (Throwable exception) {
            sender.sendMessage("§cAn error occurred: " + Util.exception(exception));
            exception.printStackTrace();
        }
        return true;
    }

}
