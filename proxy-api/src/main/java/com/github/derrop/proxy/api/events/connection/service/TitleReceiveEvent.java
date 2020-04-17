package com.github.derrop.proxy.api.events.connection.service;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class TitleReceiveEvent extends ServiceConnectionEvent implements Cancelable {

    private boolean cancel;

    private TitleUpdateType type;
    private String title;
    private String subTitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

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
