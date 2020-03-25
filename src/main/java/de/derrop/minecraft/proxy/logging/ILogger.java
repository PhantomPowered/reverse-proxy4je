package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

public interface ILogger {

    void log(LoggingLevel level, String message);

    default void info(String message) {
        this.log(LoggingLevel.INFORMATION, message);
    }

    default void warn(String message) {
        this.log(LoggingLevel.WARNING, message);
    }

    default void error(String message) {
        this.log(LoggingLevel.ERROR, message);
    }

    default void debug(String message) {
        if (this.isDebugging()) {
            this.log(LoggingLevel.DEBUG, ConsoleColor.YELLOW + message);
        }
    }

    boolean isDebugging();

    void setDebugging(boolean enable);

    void close();

    void setConsole(IConsole console);

    IConsole getConsole();

    void addHandler(LogHandler handler);

    void removeHandler(LogHandler handler);

}
