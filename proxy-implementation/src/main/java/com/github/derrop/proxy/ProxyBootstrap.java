package com.github.derrop.proxy;

import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.logging.DefaultLogger;
import com.github.derrop.proxy.logging.FileLoggerHandler;
import com.github.derrop.proxy.logging.ILogger;
import com.github.derrop.proxy.logging.JAnsiConsole;

import java.io.IOException;

public final class ProxyBootstrap {

    public static synchronized void main(String[] args) throws IOException {
        ILogger logger = new DefaultLogger(new JAnsiConsole(
                () -> String.format("&c%s&7@&fProxy &7> &e", System.getProperty("user.name"))
        ));
        logger.addHandler(new FileLoggerHandler("logs/proxy.log", 8_000_000));

        MCProxy proxy = new MCProxy(logger);
        proxy.bootstrap(25567); // TODO: config

        CommandMap commandMap = proxy.getServiceRegistry().getProviderUnchecked(CommandMap.class);
        while (!Thread.interrupted()) {
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
        }
    }
}
