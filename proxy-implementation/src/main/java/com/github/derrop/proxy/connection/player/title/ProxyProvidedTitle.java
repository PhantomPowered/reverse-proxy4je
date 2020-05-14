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
package com.github.derrop.proxy.connection.player.title;

import com.github.derrop.proxy.api.util.ProvidedTitle;
import com.github.derrop.proxy.protocol.play.server.message.PacketPlayServerTitle;
import net.kyori.text.Component;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public abstract class ProxyProvidedTitle implements ProvidedTitle {

    private static final int DEFAULT_TICK_TIME = 20;

    protected PacketPlayServerTitle title;
    protected PacketPlayServerTitle subtitle;
    protected PacketPlayServerTitle times;
    protected PacketPlayServerTitle clear;
    protected PacketPlayServerTitle reset;

    @NotNull
    private static PacketPlayServerTitle createTitle(@NotNull PacketPlayServerTitle.Action action) {
        PacketPlayServerTitle title = new PacketPlayServerTitle();
        title.setAction(action);

        if (action == PacketPlayServerTitle.Action.TIMES) {
            title.setFadeIn(DEFAULT_TICK_TIME);
            title.setStay(DEFAULT_TICK_TIME);
            title.setFadeOut(DEFAULT_TICK_TIME);
        }

        return title;
    }

    @Override
    @NotNull
    public ProvidedTitle title(@NotNull Component text) {
        if (this.title == null) {
            this.title = createTitle(PacketPlayServerTitle.Action.TITLE);
        }

        this.title.setText(GsonComponentSerializer.INSTANCE.serialize(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle subTitle(@NotNull Component text) {
        if (this.subtitle == null) {
            this.subtitle = createTitle(PacketPlayServerTitle.Action.SUBTITLE);
        }

        this.subtitle.setText(GsonComponentSerializer.INSTANCE.serialize(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle fadeIn(int ticks) {
        if (this.times == null) {
            this.times = createTitle(PacketPlayServerTitle.Action.TIMES);
        }

        this.times.setFadeIn(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle stay(int ticks) {
        if (this.times == null) {
            this.times = createTitle(PacketPlayServerTitle.Action.TIMES);
        }

        this.times.setStay(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle fadeOut(int ticks) {
        if (this.times == null) {
            this.times = createTitle(PacketPlayServerTitle.Action.TIMES);
        }

        this.times.setFadeOut(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle clear() {
        if (this.clear == null) {
            this.clear = createTitle(PacketPlayServerTitle.Action.CLEAR);
        }

        this.title = null;
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle reset() {
        if (this.reset == null) {
            this.reset = createTitle(PacketPlayServerTitle.Action.RESET);
        }

        title = null;
        subtitle = null;
        times = null;
        return this;
    }
}
