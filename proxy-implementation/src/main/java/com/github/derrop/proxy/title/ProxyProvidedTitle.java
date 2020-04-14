package com.github.derrop.proxy.title;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import net.md_5.bungee.chat.ComponentSerializer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTitle;
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
    public ProvidedTitle title(@NotNull BaseComponent text) {
        if (this.title == null) {
            this.title = createTitle(PacketPlayServerTitle.Action.TITLE);
        }

        this.title.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle title(@NotNull BaseComponent... text) {
        if (this.title == null) {
            this.title = createTitle(PacketPlayServerTitle.Action.TITLE);
        }

        this.title.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle subTitle(@NotNull BaseComponent text) {
        if (this.subtitle == null) {
            this.subtitle = createTitle(PacketPlayServerTitle.Action.SUBTITLE);
        }

        this.subtitle.setText(ComponentSerializer.toString(text));
        return this;
    }

    @Override
    @NotNull
    public ProvidedTitle subTitle(@NotNull BaseComponent... text) {
        if (this.subtitle == null) {
            this.subtitle = createTitle(PacketPlayServerTitle.Action.SUBTITLE);
        }

        this.subtitle.setText(ComponentSerializer.toString(text));
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
