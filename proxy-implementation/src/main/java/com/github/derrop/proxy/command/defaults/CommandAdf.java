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

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.player.Player;
import org.jetbrains.annotations.NotNull;

public class CommandAdf extends NonTabCompleteableCommandCallback {

    public CommandAdf() {
        super("proxy.command.adf", null);
    }

    @Override
    public boolean testPermission(@NotNull CommandSender commandSender) {
        return true;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        Player player = (Player) commandSender;

        /*Location current = player.getLocation().clone();
        Location newLoc = new Location(current.getX() + 1, current.getY() + 1, current.getZ(), current.getYaw(), current.getPitch());

        player.teleport(newLoc);*/

        if (arguments.length == 1) {
            player.sendBlockChange(player.getLocation(), Integer.parseInt(arguments[0]));
            player.sendMessage("Done: " + arguments[0]);
        } else {
            int a = 0;
            for (int i = Integer.parseInt(arguments[0]); i < Integer.parseInt(arguments[1]); i++) {
                player.sendBlockChange(player.getLocation().clone().add(a++, 0, 0), i);
                player.sendMessage("Done: " + i);
            }
        }


        return CommandResult.END;
    }
}
