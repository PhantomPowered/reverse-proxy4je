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
package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class TitleReceiveEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancel;

    private final TitleUpdateType type;
    private final String title;
    private final String subTitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleReceiveEvent(@NotNull ServiceConnection connection, String title, TitleUpdateType type) {
        this(connection, type, type == TitleUpdateType.TITLE ? title : null, type == TitleUpdateType.SUB_TITLE ? title : null, -1, -1, -1);
    }

    public TitleReceiveEvent(@NotNull ServiceConnection connection, int fadeIn, int stay, int fadeOut) {
        this(connection, TitleUpdateType.TIMES, null, null, fadeIn, stay, fadeOut);
    }

    public TitleReceiveEvent(@NotNull ServiceConnection connection) {
        this(connection, TitleUpdateType.RESET, null, null, -1, -1, -1);
    }

    private TitleReceiveEvent(@NotNull ServiceConnection connection, TitleUpdateType type, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        super(connection);
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public TitleUpdateType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    public enum TitleUpdateType {
        TITLE, SUB_TITLE, TIMES, RESET
    }

}
