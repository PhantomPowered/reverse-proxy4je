package com.github.derrop.proxy.plugins.gommecw.event;

import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarInfo;

public class GommeCWAddEvent extends Event {

    private final RunningClanWarInfo info;

    public GommeCWAddEvent(RunningClanWarInfo info) {
        this.info = info;
    }

    public RunningClanWarInfo getInfo() {
        return this.info;
    }
}
