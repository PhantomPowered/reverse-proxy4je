/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.command.defaults;

import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.player.OfflinePlayer;
import com.github.phantompowered.proxy.api.player.PlayerRepository;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CommandPermissions extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry serviceRegistry;

    public CommandPermissions(@NotNull ServiceRegistry serviceRegistry) {
        super("proxy.command.perms", null);
        this.serviceRegistry = serviceRegistry;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("perms user <name> add permission <permission>");
        sender.sendMessage("perms user <name> remove permission <permission>");
        sender.sendMessage("perms user <name> clear permissions");
        sender.sendMessage("perms user <name>");
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length < 2 || !arguments[0].equalsIgnoreCase("user")) {
            this.sendHelp(commandSender);
            return CommandResult.BREAK;
        }

        PlayerRepository playerRepository = serviceRegistry.getProviderUnchecked(PlayerRepository.class);
        if (arguments.length == 2) {
            OfflinePlayer offlinePlayer = playerRepository.getOfflinePlayer(arguments[1]);
            if (offlinePlayer == null) {
                commandSender.sendMessage("Unknown player");
                return CommandResult.BREAK;
            }

            commandSender.sendMessage("Permissions of player " + offlinePlayer.getName() + ":");
            for (Map.Entry<String, Boolean> stringBooleanEntry : offlinePlayer.getEffectivePermissions().entrySet()) {
                commandSender.sendMessage(" -> " + stringBooleanEntry.getKey() + "#" + stringBooleanEntry.getValue());
            }

            return CommandResult.END;
        }

        if (arguments.length == 4) {
            if (!arguments[2].equalsIgnoreCase("clear") && !arguments[3].equalsIgnoreCase("permissions")) {
                this.sendHelp(commandSender);
                return CommandResult.BREAK;
            }

            OfflinePlayer offlinePlayer = playerRepository.getOfflinePlayer(arguments[1]);
            if (offlinePlayer == null) {
                commandSender.sendMessage("Unknown player");
                return CommandResult.BREAK;
            }

            offlinePlayer.clearPermissions();
            playerRepository.updateOfflinePlayer(offlinePlayer);

            commandSender.sendMessage("Cleared permissions of user " + offlinePlayer.getName());
            return CommandResult.END;
        }

        if (arguments.length == 5) {
            if (arguments[2].equalsIgnoreCase("remove") && arguments[3].equalsIgnoreCase("permission")) {
                OfflinePlayer offlinePlayer = playerRepository.getOfflinePlayer(arguments[1]);
                if (offlinePlayer == null) {
                    commandSender.sendMessage("Unknown player");
                    return CommandResult.BREAK;
                }

                offlinePlayer.removePermission(arguments[4]);
                playerRepository.updateOfflinePlayer(offlinePlayer);

                commandSender.sendMessage("Removed permission " + arguments[4] + " from user " + offlinePlayer.getName());
                return CommandResult.END;
            }

            if (arguments[2].equalsIgnoreCase("add") && arguments[3].equalsIgnoreCase("permission")) {
                OfflinePlayer offlinePlayer = playerRepository.getOfflinePlayer(arguments[1]);
                if (offlinePlayer == null) {
                    commandSender.sendMessage("Unknown player");
                    return CommandResult.BREAK;
                }

                offlinePlayer.addPermission(arguments[4], true);
                playerRepository.updateOfflinePlayer(offlinePlayer);

                commandSender.sendMessage("Added permission " + arguments[4] + " to user " + offlinePlayer.getName());
                return CommandResult.END;
            }

            this.sendHelp(commandSender);
            return CommandResult.BREAK;
        }

        if (arguments.length == 6) {
            if (!arguments[2].equalsIgnoreCase("add") && !arguments[3].equalsIgnoreCase("permission")) {
                this.sendHelp(commandSender);
                return CommandResult.BREAK;
            }

            OfflinePlayer offlinePlayer = playerRepository.getOfflinePlayer(arguments[1]);
            if (offlinePlayer == null) {
                commandSender.sendMessage("Unknown player");
                return CommandResult.BREAK;
            }

            offlinePlayer.addPermission(arguments[4], arguments[5].equalsIgnoreCase("true"));
            playerRepository.updateOfflinePlayer(offlinePlayer);

            commandSender.sendMessage("Added permission " + arguments[4] + " with value " + arguments[5].equalsIgnoreCase("true")
                    + " to user " + offlinePlayer.getName());
            return CommandResult.END;
        }

        this.sendHelp(commandSender);
        return CommandResult.END;
    }
}
