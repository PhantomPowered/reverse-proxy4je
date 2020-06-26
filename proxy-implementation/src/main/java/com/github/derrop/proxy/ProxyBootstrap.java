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
import com.github.derrop.proxy.console.ProxyConsole;
import com.github.derrop.proxy.logging.ProxyLogLevels;
import com.github.derrop.proxy.logging.ProxyLogger;

public final class ProxyBootstrap {

    public static synchronized void main(String[] args) {
        ProxyConsole proxyConsole = new ProxyConsole();
        ProxyLogger proxyLogger = new ProxyLogger(proxyConsole.getLineReader());

        sendWelcomeScreen();

        long start = System.currentTimeMillis();
        MCProxy proxy = new MCProxy();
        proxy.getServiceRegistry().setProvider(null, ProxyLogger.class, proxyLogger, true);
        proxy.getServiceRegistry().setProvider(null, ProxyConsole.class, proxyConsole, true);
        proxy.bootstrap(proxy.getServiceRegistry().getProviderUnchecked(Configuration.class).getProxyPort(), start);

        CommandMap commandMap = proxy.getServiceRegistry().getProviderUnchecked(CommandMap.class);
        CommandSender sender = new ConsoleCommandSender(proxy.getServiceRegistry());

        String line;
        while (!((line = proxyConsole.readString()).isEmpty())) {
            try {
                if (commandMap.process(sender, line) == CommandResult.NOT_FOUND) {
                    proxyLogger.logp(ProxyLogLevels.COMMAND, "", "", "Unable to find command by this name. Use \"help\" to get a list of all commands");
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static void sendWelcomeScreen() {
        System.out.println("\n" +
                "   ____        _ _            __  __  _____ _____                     \n" +
                "  / __ \\      | (_)          |  \\/  |/ ____|  __ \\                    \n" +
                " | |  | |_ __ | |_ _ __   ___| \\  / | |    | |__) | __ _____  ___   _ \n" +
                " | |  | | '_ \\| | | '_ \\ / _ \\ |\\/| | |    |  ___/ '__/ _ \\ \\/ / | | |\n" +
                " | |__| | | | | | | | | |  __/ |  | | |____| |   | | | (_) >  <| |_| |\n" +
                "  \\____/|_| |_|_|_|_| |_|\\___|_|  |_|\\_____|_|   |_|  \\___/_/\\_\\\\__, |\n" +
                "                                                                 __/ |\n" +
                "                                                                |___/ \n");
        System.out.println("Booting up online mc proxy version " + ProxyBootstrap.class.getPackage().getImplementationVersion() + " by derrop and derklaro");
        System.out.println("Running on runtime " + System.getProperty("java.runtime.name") + " in vm " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.info"));
        System.out.println("Java Version: " + System.getProperty("java.runtime.version") + " (vm: " + System.getProperty("java.vm.version") + ")" + " system: " + System.getProperty("os.name") + " (arch: " + System.getProperty("os.arch") + ")");
        System.out.println();
    }
}
