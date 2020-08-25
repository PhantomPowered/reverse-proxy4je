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
package com.github.derrop.proxy.api.chat;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {

    BLACK('0', "black"),
    DARK_BLUE('1', "dark_blue"),
    DARK_GREEN('2', "dark_green"),
    DARK_AQUA('3', "dark_aqua"),
    DARK_RED('4', "dark_red"),
    DARK_PURPLE('5', "dark_purple"),
    GOLD('6', "gold"),
    GRAY('7', "gray"),
    DARK_GRAY('8', "dark_gray"),
    BLUE('9', "blue"),
    GREEN('a', "green"),
    AQUA('b', "aqua"),
    RED('c', "red"),
    LIGHT_PURPLE('d', "light_purple"),
    YELLOW('e', "yellow"),
    WHITE('f', "white"),

    MAGIC('k', "obfuscated", true),
    BOLD('l', "bold", true),
    STRIKETHROUGH('m', "strikethrough", true),
    UNDERLINE('n', "underline", true),
    ITALIC('o', "italic", true),
    RESET('r', "reset", true);

    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    private static final Map<Character, ChatColor> BY_CHAR = new HashMap<>();

    private final char code;
    private final String toString;
    private final String name;
    private final boolean isColor;

    static {
        for (ChatColor colour : values()) {
            BY_CHAR.put(colour.code, colour);
        }
    }

    ChatColor(char code, String name) {
        this(code, name, false);
    }

    ChatColor(char code, String name, boolean noColor) {
        this.code = code;
        this.name = name;
        this.toString = new String(new char[]{COLOR_CHAR, code});
        this.isColor = !noColor;
    }

    public boolean isColor() {
        return this.isColor;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return toString;
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    @NotNull
    public static String getLastColors(@NotNull String input) {
        Validate.notNull(input, "Cannot get last colors from null text");

        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = getByChar(c);

                if (color != null) {
                    result.insert(0, color.toString());
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

    public static ChatColor getLastColor(final String input) {
        int i = input.lastIndexOf(COLOR_CHAR);
        if (i == -1 || i == input.length() - 2) {
            return null;
        }

        return getByChar(input.charAt(i + 1));
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }
}
