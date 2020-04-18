package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.player.PlayerLogoutEvent;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.entity.player.DefaultPlayer;
import com.github.derrop.proxy.network.channel.ChannelListener;
import net.md_5.bungee.Util;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClientPacketListener implements ChannelListener {

    private final DefaultPlayer player;

    public ClientPacketListener(DefaultPlayer player) {
        this.player = player;
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (!(cause instanceof IOException)) {
            this.player.sendMessage("Unexpected exception, check log for more details: " + Util.exception(cause));
            cause.printStackTrace();
        }
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        this.player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerLogoutEvent(player));
        this.player.setConnected(false);
        if (this.player.getConnectedClient() != null) {
            this.player.getConnectedClient().getClient().free();
        }
    }

}
