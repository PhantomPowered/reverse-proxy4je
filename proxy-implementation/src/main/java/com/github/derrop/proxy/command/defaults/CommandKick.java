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
package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.repository.PlayerRepository;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandKick extends NonTabCompleteableCommandCallback {

    public CommandKick() {
        super("proxy.command.kick", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length < 1) {
            commandSender.sendMessage("kick <name> [message]");
            return CommandResult.BREAK;
        }

        Player player = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(PlayerRepository.class).getOnlinePlayer(arguments[0]);
        if (player == null) {
            commandSender.sendMessage("§cThat player isn't online");
            return CommandResult.BREAK;
        }

        String message = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        if (message.trim().isEmpty()) {
            message = "No reason given";
        }

        message = "§7Kicked by §e" + commandSender.getName() + "§7. Reason: " + ChatColor.translateAlternateColorCodes('&', message);
        player.disconnect(TextComponent.of(message));
        return CommandResult.END;
    }
}
