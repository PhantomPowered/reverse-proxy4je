package de.derrop.minecraft.proxy.api.command;

import java.util.Collection;

public interface CommandMap {

    String getPrefix();

    void registerCommand(Command command);

    Collection<Command> getCommands();

    boolean dispatchCommand(String line);

    boolean dispatchCommand(CommandSender sender, String line);

}
