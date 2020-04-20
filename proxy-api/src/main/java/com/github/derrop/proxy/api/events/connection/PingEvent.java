package com.github.derrop.proxy.api.events.connection;

import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.ping.ServerPing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PingEvent extends Event {

    private NetworkChannel channel;
    private ServerPing response;

    public PingEvent(@NotNull NetworkChannel channel, @Nullable ServerPing response) {
        this.channel = channel;
        this.response = response;
    }

    @NotNull
    public NetworkChannel getChannel() {
        return this.channel;
    }

    @Nullable
    public ServerPing getResponse() {
        return this.response;
    }

    public void setResponse(@Nullable ServerPing response) {
        this.response = response;
    }
}
