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

public final class ClickEvent {

    /**
     * The type of action to perform on click.
     */
    private final Action action;
    /**
     * Depends on the action.
     *
     * @see Action
     */
    private final String value;

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "ClickEvent(action=" + this.getAction() + ", value=" + this.getValue() + ")";
    }

    public enum Action {

        /**
         * Open a url at the path given by
         * {@link ClickEvent#value}.
         */
        OPEN_URL,
        /**
         * Open a file at the path given by
         * {@link ClickEvent#value}.
         */
        OPEN_FILE,
        /**
         * Run the command given by
         * {@link ClickEvent#value}.
         */
        RUN_COMMAND,
        /**
         * Inserts the string given by
         * {@link ClickEvent#value} into the player's
         * text box.
         */
        SUGGEST_COMMAND,
        /**
         * Change to the page number given by
         * {@link ClickEvent#value} in a book.
         */
        CHANGE_PAGE,
        /**
         * Copy the string given by
         * {@link ClickEvent#value} into the player's
         * clipboard.
         */
        COPY_TO_CLIPBOARD
    }
}
