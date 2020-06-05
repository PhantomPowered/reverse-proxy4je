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
package com.github.derrop.proxy;

import com.github.derrop.proxy.api.Configuration;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.command.console.ConsoleCommandSender;

import java.io.Console;
import java.io.IOException;

public final class ProxyBootstrap {

    public static synchronized void main(String[] args) throws IOException {
        /*ILogger logger = new DefaultLogger(new JAnsiConsole(
                () -> String.format("&c%s&7@&fProxy &7> &e", System.getProperty("user.name"))
        ));
        logger.addHandler(new FileLoggerHandler("logs/proxy.log", 8_000_000));*/

        MCProxy proxy = new MCProxy();
        proxy.bootstrap(proxy.getServiceRegistry().getProviderUnchecked(Configuration.class).getProxyPort());

        CommandMap commandMap = proxy.getServiceRegistry().getProviderUnchecked(CommandMap.class);
        /*while (!Thread.interrupted()) {
            String line = logger.getConsole().readLine().join();
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            try {
                if (commandMap.process(logger.getConsole().getConsoleCommandSender(), line) == CommandResult.NOT_FOUND) {
                    logger.warn("Unable to find command by this name. Use \"help\" to get a list of all commands");
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                logger.warn(ex.getMessage());
            }
        }*/
        CommandSender sender = new ConsoleCommandSender();
        Console console = System.console();
        if (console == null) {
            System.err.println("Console not supported! No commands can be executed in the console");
            return;
        }
        String line;
        while ((line = console.readLine()) != null) {
            try {
                if (commandMap.process(sender, line) == CommandResult.NOT_FOUND) {
                    System.out.println("Unable to find command by this name. Use \"help\" to get a list of all commands");
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
