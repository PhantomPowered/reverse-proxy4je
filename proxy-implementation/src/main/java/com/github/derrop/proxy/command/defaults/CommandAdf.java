package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.location.Location;
import org.jetbrains.annotations.NotNull;

public class CommandAdf extends NonTabCompleteableCommandCallback {

    public CommandAdf() {
        super("proxy.command.adf", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        Player player = (Player) commandSender;

        Location current = player.getLocation().clone();
        Location newLoc = new Location(current.getX() + 1, current.getY() + 1, current.getZ(), current.getYaw(), current.getPitch());

        player.setLocation(newLoc);
        return CommandResult.END;
    }
}
