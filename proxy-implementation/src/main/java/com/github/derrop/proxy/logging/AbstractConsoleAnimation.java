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
