package com.github.derrop.proxy.title;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.Title;
import org.jetbrains.annotations.NotNull;

public abstract class ProxyProvidedTitle implements ProvidedTitle {

    private static final int DEFAULT_TICK_TIME = 20;

    protected Title title;
    protected Title subtitle;
    protected Title times;
    protected Title clear;
    protected Title reset;

    @NotNull
    private static Title createTitle(@NotNull Title.Action action) {
        Title title = new Title();
        title.setAction(action);

        if (action == Title.Action.TIMES) {
            title.setFadeIn(DEFAULT_TICK_TIME);
            title.setStay(DEFAULT_TICK_TIME);
            title.setFadeOut(DEFAULT_TICK_TIME);
        }

        return title;
    }

    @Override
    @NotNull
    public ProvidedTitle title(@NotNull BaseComponent text) {
        if (this.title == null) {
            this.title = createTitle(Title.Action.TITLE);
        }

        this.title.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle title(@NotNull BaseComponent... text) {
        if (this.title == null) {
            this.title = createTitle(Title.Action.TITLE);
        }

        this.title.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle subTitle(@NotNull BaseComponent text) {
        if (this.subtitle == null) {
            this.subtitle = createTitle(Title.Action.SUBTITLE);
        }

        this.subtitle.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle subTitle(@NotNull BaseComponent... text) {
        if (this.subtitle == null) {
            this.subtitle = createTitle(Title.Action.SUBTITLE);
        }

        this.subtitle.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle fadeIn(int ticks) {
        if (this.times == null) {
            this.times = createTitle(Title.Action.TIMES);
        }

        this.times.setFadeIn(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle stay(int ticks) {
        if (this.times == null) {
            this.times = createTitle(Title.Action.TIMES);
        }

        this.times.setStay(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle fadeOut(int ticks) {
        if (this.times == null) {
            this.times = createTitle(Title.Action.TIMES);
        }

        this.times.setFadeOut(ticks);
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle clear() {
        if (this.clear == null) {
            this.clear = createTitle(Title.Action.CLEAR);
        }

        this.title = null;
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle reset() {
        if (this.reset == null) {
            this.reset = createTitle(Title.Action.RESET);
        }

        title = null;
        subtitle = null;
        times = null;
        return this;
    }
}
