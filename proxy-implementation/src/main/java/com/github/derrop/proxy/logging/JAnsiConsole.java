package com.github.derrop.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.command.console.ConsoleCommandSender;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JAnsiConsole implements IConsole {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final CommandSender COMMAND_SENDER = new ConsoleCommandSender();

    private ConsoleReader consoleReader;
    private Supplier<String> promptSupplier;
    private JAnsiCompleter completer;
    private Map<String, Consumer<String>> lineConsumers = new HashMap<>();
    private Thread lineReader;

    @Getter
    private AbstractConsoleAnimation runningAnimation;
    private Thread animationThread;
    private boolean running;

    public JAnsiConsole() throws IOException {
        this(null);
    }

    public JAnsiConsole(Supplier<String> promptSupplier) throws IOException {
        this(System.in, System.out, promptSupplier);
    }

    public JAnsiConsole(InputStream inputStream, OutputStream outputStream, Supplier<String> promptSupplier) throws IOException {
        this(new ConsoleReader(inputStream, outputStream), promptSupplier);
    }

    public JAnsiConsole(ConsoleReader consoleReader, Supplier<String> promptSupplier) {
        this.consoleReader = consoleReader;
        this.promptSupplier = () -> ConsoleColor.translateAlternateColorCodes(promptSupplier.get());

        this.lineReader = new Thread(() -> {
            while (!Thread.interrupted() && this.running) {
                try {
                    String line = null;
                    if (this.promptSupplier != null && this.promptSupplier.get() != null) {
                        line = this.consoleReader.readLine(this.promptSupplier.get());
                        this.consoleReader.setPrompt(ConsoleColor.RESET.toString());
                    } else {
                        line = this.consoleReader.readLine();
                    }
                    if (line != null) {
                        this.handleConsoleInput(line);
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "JAnsiConsole input reader");
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void write(String s) {
        int i = this.write0(s);
        if (this.runningAnimation != null && i > 0) {
            this.runningAnimation.cursorUp += i;
        }

        if (this.runningAnimation != null) {
            this.runningAnimation.cursorUp++;
        }
    }

    private int write0(String s) {
        s = ConsoleColor.translateAlternateColorCodes(s);

        int i = 0;
        try {
            for (String s1 : s.split("\n")) {
                String[] a = s1.split("\r");
                i = a.length;
                for (String s2 : a) {
                    this.consoleReader.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + s2 + ConsoleColor.RESET.toString());
                    this.consoleReader.drawLine();
                    this.consoleReader.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;

    }

    @Override
    public void writeln(String s) {
        s = ConsoleColor.translateAlternateColorCodes(s);

        int i = 0;
        try {
            for (String s1 : s.split("\n")) {
                String[] a = s1.split("\r");
                i = a.length;
                for (String s2 : a) {
                    this.consoleReader.println(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + s2 + ConsoleColor.RESET.toString());
                    this.consoleReader.drawLine();
                    this.consoleReader.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.runningAnimation != null && i > 0) {
            this.runningAnimation.cursorUp += i;
        }
    }

    @Override
    public void setPrompt(String prompt) {
        this.promptSupplier = () -> ConsoleColor.translateAlternateColorCodes(prompt);
    }

    @Override
    public String getPrompt() {
        return this.promptSupplier == null ? null : this.promptSupplier.get();
    }

    @Override
    public void handleConsoleInput(String line) {
        line = line.trim();
        for (Consumer<String> lineConsumer : new HashSet<>(this.lineConsumers.values())) {
            lineConsumer.accept(line);
        }
    }

    @Override
    public CompletableFuture<String> readLine() {
        return this.readLineUntil(s -> true, false);
    }

    @Override
    public CompletableFuture<String> readLineUntil(Predicate<String> predicate, boolean allowCancel) {
        String key = UUID.randomUUID().toString();
        CompletableFuture<String> task = new CompletableFuture<>();
        StringBuilder builder = new StringBuilder();
        this.lineConsumers.put(key, s -> {
            if (allowCancel && s.equalsIgnoreCase("cancel")) {
                task.complete(null);
                this.lineConsumers.remove(key);
            } else if (predicate.test(s)) {
                builder.append(s);
                task.complete(builder.toString());
                this.lineConsumers.remove(key);
            } else {
                builder.append(s).append(' ');
            }
        });
        return task;
    }

    @Override
    public boolean isSupportingColored() {
        return true;
    }

    @Override
    public void clearScreen() {
        try {
            this.consoleReader.clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() {
        AnsiConsole.systemInstall();
        this.consoleReader.addCompleter(this.completer = new JAnsiCompleter());
        this.running = true;
        this.lineReader.start();
    }

    @Override
    public void close() {
        this.lineReader.interrupt();
        this.lineReader.stop();
        this.running = false;

        this.consoleReader.close();

        AnsiConsole.systemUninstall();
    }

    @Override
    public boolean isSupportingTabCompletion() {
        return true;
    }

    @Override
    public void addTabCompleter(String key, ITabCompletable tabCompletable) {
        if (this.completer.getCompleters().containsKey(key))
            throw new IllegalStateException(key + " is already registered");
        this.completer.getCompleters().put(key, new TabCompletableWrapper(true, tabCompletable));
    }

    @Override
    public void removeTabCompleter(String key) {
        if (!this.completer.getCompleters().containsKey(key))
            throw new IllegalStateException(key + " is not registered");
        this.completer.getCompleters().remove(key);

    }

    @Override
    public void disableTabCompleters() {
        for (TabCompletableWrapper value : this.completer.getCompleters().values()) {
            value.setEnabled(false);
        }
    }

    @Override
    public void enableTabCompleters() {
        for (TabCompletableWrapper value : this.completer.getCompleters().values()) {
            value.setEnabled(true);
        }
    }

    @Override
    public void addLineHandler(String key, Consumer<String> lineConsumer) {
        this.addSyncLineHandler(key, s -> EXECUTOR_SERVICE.execute(() -> lineConsumer.accept(s)));
    }

    @Override
    public void addSyncLineHandler(String key, Consumer<String> lineConsumer) {
        if (this.lineConsumers.containsKey(key))
            throw new IllegalStateException(key + " is already registered");
        this.lineConsumers.put(key, lineConsumer);
    }

    @Override
    public void removeLineHandler(String key) {
        if (!this.lineConsumers.containsKey(key))
            throw new IllegalStateException(key + " is not registered");
        this.lineConsumers.remove(key);
    }

    @Override
    public void startAnimation(AbstractConsoleAnimation animation) {
        if (this.runningAnimation != null)
            throw new IllegalStateException("There is already another animation running!");

        if (this.animationThread != null) {
            this.animationThread.interrupt();
        }

        animation.setConsole(this);
        animation.printRaw = this::write0;

        this.runningAnimation = animation;

        this.animationThread = new Thread(() -> {
            animation.run();
            this.runningAnimation = null;
        }, animation.getClass().getName() + " - AnimationThread");
        this.animationThread.start();
    }

    @Override
    public CommandSender getConsoleCommandSender() {
        return COMMAND_SENDER;
    }
    /*
    public static InputStream createTimeOutUsingInputStream(InputStream inputStream, long timeout) {
        return new TimeOutInputStream(inputStream, timeout);
    }

    private static class TimeOutInputStream extends InputStream {

        private InputStream original;
        private long timeout;
        private boolean interrupted = false;

        public TimeOutInputStream(InputStream original, long timeout) {
            this.original = original;
            this.timeout = timeout;
        }

        public void setInterrupted(boolean interrupted) {
            this.interrupted = interrupted;
        }

        @Override
        public int read() throws IOException {
            if (this.original.available() > 0) {
                return this.original.read();
            } else {
                if (this.interrupted) {
                    return -1;
                }
                try {
                    Thread.sleep(this.timeout);
                } catch (InterruptedException e) {
                    return -1;
                }
                return this.read();
            }
        }

    }*/

}
