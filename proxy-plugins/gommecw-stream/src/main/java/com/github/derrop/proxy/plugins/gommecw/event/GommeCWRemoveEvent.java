package com.github.derrop.proxy.plugins.gommecw.event;

import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarInfo;

public class GommeCWRemoveEvent extends Event {

    private final RunningClanWarInfo info;

    public GommeCWRemoveEvent(RunningClanWarInfo info) {
        this.info = info;
    }

    public RunningClanWarInfo getInfo() {
        return this.info;
    }
}
