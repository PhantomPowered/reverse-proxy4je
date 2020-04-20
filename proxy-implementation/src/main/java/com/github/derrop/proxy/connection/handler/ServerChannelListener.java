package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.util.Utils;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ServerChannelListener implements ChannelListener {

    private final ConnectedProxyClient client;

    public ServerChannelListener(ConnectedProxyClient client) {
        this.client = client;
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        System.err.println("Exception on proxy client " + this.client.getAccountName() + "!");
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }

        MCProxy.getInstance().unregisterConnection(this.client.getConnection());
        this.client.handleDisconnect(TextComponent.of("§c" + Utils.stringifyException(cause)));
        this.client.getConnection().close();
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        this.client.getProxy().unregisterConnection(this.client.getConnection());
        this.client.handleDisconnect(TextComponent.of("§cNo reason given"));
    }

}
