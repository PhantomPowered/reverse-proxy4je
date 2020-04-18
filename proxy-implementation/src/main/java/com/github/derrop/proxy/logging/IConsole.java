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
