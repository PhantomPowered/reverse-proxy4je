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
package com.github.phantompowered.proxy.api.entity;

public enum EnumColor {
    WHITE(0, 15, "white", "white"),
    ORANGE(1, 14, "orange", "orange"),
    MAGENTA(2, 13, "magenta", "magenta"),
    LIGHT_BLUE(3, 12, "light_blue", "lightBlue"),
    YELLOW(4, 11, "yellow", "yellow"),
    LIME(5, 10, "lime", "lime"),
    PINK(6, 9, "pink", "pink"),
    GRAY(7, 8, "gray", "gray"),
    SILVER(8, 7, "silver", "silver"),
    CYAN(9, 6, "cyan", "cyan"),
    PURPLE(10, 5, "purple", "purple"),
    BLUE(11, 4, "blue", "blue"),
    BROWN(12, 3, "brown", "brown"),
    GREEN(13, 2, "green", "green"),
    RED(14, 1, "red", "red"),
    BLACK(15, 0, "black", "black");

    private static final EnumColor[] COLOR = new EnumColor[EnumColor.values().length];
    private static final EnumColor[] COLOR_INV = new EnumColor[EnumColor.values().length];

    static {
        for (EnumColor color : values()) {
            COLOR[color.getColorIndex()] = color;
            COLOR_INV[color.getInvColorIndex()] = color;
        }
    }

    private final int colorIndex;
    private final int invColorIndex;
    private final String colorName;
    private final String invColorName;

    EnumColor(int colorIndex, int invColorIndex, String colorName, String invColorName) {
        this.colorIndex = colorIndex;
        this.invColorIndex = invColorIndex;
        this.colorName = colorName;
        this.invColorName = invColorName;
    }

    public static EnumColor getById(int colorIndex) {
        if (colorIndex < 0 || colorIndex >= COLOR.length) {
            colorIndex = 0;
        }

        return COLOR[colorIndex];
    }

    public static EnumColor getByInvIndex(int invColorIndex) {
        if (invColorIndex < 0 || invColorIndex >= COLOR_INV.length) {
            invColorIndex = 0;
        }

        return COLOR_INV[invColorIndex];
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public int getInvColorIndex() {
        return invColorIndex;
    }

    public String getColorName() {
        return colorName;
    }

    public String getInvColorName() {
        return invColorName;
    }
}
