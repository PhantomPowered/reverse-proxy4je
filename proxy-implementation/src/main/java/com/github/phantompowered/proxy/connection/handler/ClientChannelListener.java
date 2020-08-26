package com.github.phantompowered.proxy.connection.handler;

import com.github.phantompowered.proxy.ImplementationUtil;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerLogoutEvent;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.connection.BasicServiceConnection;
import com.github.phantompowered.proxy.connection.player.DefaultPlayer;
import com.github.phantompowered.proxy.network.channel.ChannelListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClientChannelListener implements ChannelListener {

    private final DefaultPlayer player;

    public ClientChannelListener(DefaultPlayer player) {
        this.player = player;
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (!(cause instanceof IOException)) {
            this.player.sendMessage("Unexpected exception, check log for more details: " + ImplementationUtil.stringifyException(cause));
            cause.printStackTrace();
        }
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        this.player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerLogoutEvent(player));
        this.player.setConnected(false);
        if (this.player.getConnectedClient() != null && this.player.getConnectedClient() instanceof BasicServiceConnection) {
            ((BasicServiceConnection) this.player.getConnectedClient()).getClient().free();
        }
    }

}
