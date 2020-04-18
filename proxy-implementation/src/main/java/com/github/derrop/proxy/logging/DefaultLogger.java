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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class DefaultLogger implements ILogger {

    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");

    @Getter
    private IConsole console;

    @Getter
    @Setter
    private boolean debugging = false;

    private Collection<LogHandler> handlers = new ArrayList<>();

    private PrintStream[] consoleStreams;

    public DefaultLogger(IConsole console) {
        this.console = console;
        this.console.open();

        consoleStreams = new PrintStream[]{
                new AsyncPrintStream(new LoggingOutputStream(LoggingLevel.INFORMATION), true),
                new AsyncPrintStream(new LoggingOutputStream(LoggingLevel.ERROR), true)
        };
        System.setOut(consoleStreams[0]);
        System.setErr(consoleStreams[1]);
    }

    @Override
    public void log(LoggingLevel level, String message) {

        if (level.getColor() != null) {
            message = level.getColor().toString() + message.replace("&r", level.getColor().toString());
        }

        String s = ConsoleColor.GRAY.toString() +
                '[' +
                ConsoleColor.GREEN.toString() +
                DEFAULT_DATE_FORMAT.format(new Date()) +
                ConsoleColor.GRAY.toString() +
                '/' +
                ConsoleColor.YELLOW.toString() +
                level.toString() +
                ConsoleColor.GRAY.toString() +
                "] " +
                ConsoleColor.RESET.toString() +
                ConsoleColor.translateAlternateColorCodes(message);

        for (LogHandler handler : this.handlers) {
            handler.handleLine(message, s);
        }

        this.console.writeln(s);
    }

    @Override
    public void close() {
        for (LogHandler handler : this.handlers) {
            handler.close();
        }

        for (PrintStream consoleStream : this.consoleStreams) {
            consoleStream.close();
        }

        this.console.close();
        this.handlers.clear();
    }

    @Override
    public void addHandler(LogHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void removeHandler(LogHandler handler) {
        this.handlers.remove(handler);
    }

    @Override
    public void setConsole(IConsole console) {
        if (console == null)
            throw new NullPointerException("console");
        if (this.console != null) {
            this.console.close();
        }
        this.console = console;
        this.console.open();
    }

    @AllArgsConstructor
    private class LoggingOutputStream extends ByteArrayOutputStream {

        private LoggingLevel level;

        @Override
        public void flush() throws IOException {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(System.lineSeparator())) {
                log(this.level, contents);
            }
        }
    }

}
