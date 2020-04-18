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
 * Created by Mc_Ruben on 17.02.2019
 */

import jline.console.completer.Completer;
import lombok.Getter;

import java.util.*;

public class JAnsiCompleter implements Completer {

    @Getter
    private Map<String, TabCompletableWrapper> completers = new HashMap<>();

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Collection<String> suggestions = new ArrayList<>();

        String[] args = buffer.split(" ");

        for (TabCompletableWrapper value : this.completers.values()) {
            Collection<String> a = value.tabComplete(buffer, args);
            if (a != null && !a.isEmpty()) {
                suggestions.addAll(a);
            }
        }

        String testString = buffer.endsWith(" ") ? "" : args[args.length - 1].toLowerCase().trim();

        if (!suggestions.isEmpty()) {
            for (String suggestion : suggestions) {
                if (testString.isEmpty() || suggestion.toLowerCase().startsWith(testString)) {
                    candidates.add(suggestion);
                }
            }
        }

        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }
}
