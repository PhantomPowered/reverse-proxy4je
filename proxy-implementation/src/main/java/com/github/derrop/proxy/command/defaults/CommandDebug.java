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
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.logging.ProxyLogger;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandDebug extends NonTabCompleteableCommandCallback {

    public CommandDebug(ServiceRegistry registry) {
        super("proxy.command.debug", null);
        this.logger = registry.getProviderUnchecked(ProxyLogger.class);
    }

    private final Logger logger;

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender commandSender, @NotNull String[] arguments, @NotNull String fullLine) throws CommandExecutionException {
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("dev")) {
            logger.setLevel(Level.ALL);
            commandSender.sendMessage("ENTERING DEVELOPER MODER - THIS IS NOT RECOMMENDED TO USE IN PRODUCTION");
            commandSender.sendMessage("Debugging level set to " + Level.ALL.getLocalizedName());
            return CommandResult.BREAK;
        }

        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("basic")) {
            logger.setLevel(Level.FINE);
            commandSender.sendMessage("Debugging level set to " + Level.FINE.getLocalizedName());
            return CommandResult.BREAK;
        }

        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("basic-net")) {
            logger.setLevel(Level.FINER);
            commandSender.sendMessage("Debugging level set to " + Level.FINER.getLocalizedName());
            return CommandResult.BREAK;
        }

        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("off")) {
            logger.setLevel(Level.INFO);
            commandSender.sendMessage("Debugging level set to " + Level.INFO.getLocalizedName());
            return CommandResult.BREAK;
        }

        commandSender.sendMessage("debug off");
        commandSender.sendMessage("debug dev");
        commandSender.sendMessage("debug basic");
        commandSender.sendMessage("debug basic-net");
        return CommandResult.END;
    }
}
