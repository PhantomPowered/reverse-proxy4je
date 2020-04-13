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
