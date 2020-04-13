package com.github.derrop.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

import com.github.derrop.proxy.api.command.sender.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IConsole {

    boolean isRunning();

    void write(String s);

    void writeln(String s);

    void setPrompt(String prompt);

    String getPrompt();

    void handleConsoleInput(String line);

    CompletableFuture<String> readLine();

    CompletableFuture<String> readLineUntil(Predicate<String> predicate, boolean allowCancel);

    boolean isSupportingColored();

    void clearScreen();

    void open();

    void close();

    boolean isSupportingTabCompletion();

    void addTabCompleter(String key, ITabCompletable tabCompletable);

    void removeTabCompleter(String key);

    void disableTabCompleters();

    void enableTabCompleters();

    void addLineHandler(String key, Consumer<String> lineConsumer);

    @Deprecated //deprecated because if you use the readLine method in this Consumer, it won't work
    void addSyncLineHandler(String key, Consumer<String> lineConsumer);

    void removeLineHandler(String key);

    AbstractConsoleAnimation getRunningAnimation();

    void startAnimation(AbstractConsoleAnimation animation);

    CommandSender getConsoleCommandSender();

}
