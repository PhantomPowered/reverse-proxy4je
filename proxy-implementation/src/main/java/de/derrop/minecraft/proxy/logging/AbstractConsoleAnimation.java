package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 08.03.2019
 */

import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.util.function.Consumer;

public abstract class AbstractConsoleAnimation implements Runnable {

    @Getter
    private IConsole console;
    @Getter
    private int updateInterval = 25;
    @Getter
    private long startTime;
    @Getter
    public Consumer<String> printRaw;
    public int cursorUp = 1;

    public long getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    protected void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setConsole(IConsole console) {
        this.console = console;
    }

    protected void print(String... input) {
        if (input.length == 0)
            return;
        input[0] = "&e" + input[0];
        Ansi ansi = Ansi
                .ansi()
                .saveCursorPosition()
                .cursorUp(this.cursorUp)
                .eraseLine(Ansi.Erase.ALL);
        for (String a : input) {
            ansi.a(a);
        }
        this.printRaw.accept(
                Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE +
                        ansi
                                .restoreCursorPosition()
                                .toString()
        );
    }

    protected abstract boolean handleTick(); //returns true if the animation is finished and should be cancelled

    @Override
    public final void run() {
        this.startTime = System.currentTimeMillis();
        while (!Thread.interrupted() && !handleTick()) {
            try {
                Thread.sleep(this.updateInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
