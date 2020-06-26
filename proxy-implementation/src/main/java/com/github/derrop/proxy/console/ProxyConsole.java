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
package com.github.derrop.proxy.console;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyConsole implements AutoCloseable {

    private static final String USER = System.getProperty("user.name");
    private static final String PROMPT = "Proxy@" + USER + " => ";

    private final Terminal terminal;
    private final LineReader lineReader;

    public ProxyConsole() {
        try {
            Logger.getLogger("org.jline").setLevel(Level.OFF); // disable jline-3 logger

            this.terminal = TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build();
            this.lineReader = LineReaderBuilder.builder().terminal(this.terminal).option(LineReader.Option.DISABLE_EVENT_EXPANSION, true).build();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public @NotNull String readString() {
        try {
            return this.lineReader.readLine(PROMPT);
        } catch (EndOfFileException ignored) {
        } catch (UserInterruptException ex) {
            System.exit(-2);
        }

        return "";
    }

    public void clearScreen() { // TODO: clear command
        this.terminal.puts(InfoCmp.Capability.clear_screen);
        this.terminal.flush();
    }

    @NotNull
    public LineReader getLineReader() {
        return lineReader;
    }

    @Override
    public void close() throws Exception {
        this.terminal.flush();
        this.terminal.close();
    }
}
