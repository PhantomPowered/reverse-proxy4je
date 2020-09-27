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
package com.github.phantompowered.proxy.logging;

import com.github.phantompowered.proxy.api.configuration.Configuration;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");

    private final ServiceRegistry registry;
    private final boolean raw;

    public LogFormatter(ServiceRegistry registry, boolean raw) {
        this.registry = registry;
        this.raw = raw;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(FORMAT.format(record.getMillis()));
        stringBuilder.append(" [");
        this.appendThread(stringBuilder, record.getThreadID());
        stringBuilder.append(record.getLevel().getLocalizedName());
        stringBuilder.append("] ");

        boolean privateMode = !this.raw && this.registry.getProvider(Configuration.class).map(Configuration::isPrivateMode).orElse(false);
        String message = super.formatMessage(record);
        if (privateMode) {
            String[] words = message.split(" ");
            for (String word : words) {
                int at = word.indexOf('@');
                int dot = word.indexOf('.');
                if (at > 1 && dot > at && dot < word.length() - 1) {

                    stringBuilder.append("*".repeat(at));
                    stringBuilder.append('@');
                    stringBuilder.append("*".repeat(dot - at - 1));
                    stringBuilder.append('.');
                    stringBuilder.append("*".repeat(word.length() - dot - 1));

                } else {
                    stringBuilder.append(word);
                }

                stringBuilder.append(' ');
            }

        } else {
            stringBuilder.append(message);
        }

        stringBuilder.append('\n');

        if (record.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(stringWriter));
            stringBuilder.append(stringWriter);
        }

        return stringBuilder.toString();
    }

    private void appendThread(StringBuilder builder, int id) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getId() == id) {
                builder.append(thread.getName()).append("/");
                break;
            }
        }
    }
}
