package com.github.derrop.proxy.plugins.gommecw.command;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.plugins.gommecw.GommeCWPlugin;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWar;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarInfo;
import org.jetbrains.annotations.NotNull;

public class CommandCW extends NonTabCompleteableCommandCallback {

    private final GommeCWPlugin plugin;

    public CommandCW(GommeCWPlugin plugin) {
        super("command.cw", null);
        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (args.length == 2 && args[0].equalsIgnoreCase("stream")) {

            String matchId = args[1];

            if (this.plugin.getCwManager().getClanWar(matchId) != null) {
                sender.sendMessage("A clan war with that ID is already being streamed");
                return CommandResult.BREAK;
            }

            RunningClanWarInfo info = this.plugin.getWebParser().getCachedRunningInfo(matchId).orElse(null);
            if (info == null) {
                sender.sendMessage("There is no clan war with that id running");
                return CommandResult.BREAK;
            }

            sender.sendMessage("Beginning the stream...");
            this.plugin.getWebParser().beginClanWar(info);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("end")) {

            String matchId = args[1];

            RunningClanWar clanWar = this.plugin.getCwManager().getClanWar(matchId);
            if (clanWar == null) {
                sender.sendMessage("A clan war with that ID is not being streamed");
                return CommandResult.BREAK;
            }

            sender.sendMessage("Ending the stream...");
            this.plugin.getWebParser().endClanWar(clanWar.getInfo());

        } else {
            this.sendHelp(sender);
        }

        return CommandResult.END;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("cw stream <id>");
        sender.sendMessage("cw end <id>");
    }

}
