package com.github.derrop.proxy.connection.login;

import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.network.channel.ChannelListener;
import org.jetbrains.annotations.NotNull;

public class ProxyClientLoginListener implements ChannelListener {

    private ConnectedProxyClient client;

    public ProxyClientLoginListener(ConnectedProxyClient client) {
        this.client = client;
    }

    @Override
    public void handleChannelInactive(@NotNull NetworkChannel channel) {
        this.client.getProxy().unregisterConnection(this.client.getConnection());
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void handleChannelActive(@NotNull NetworkChannel channel) {
        System.out.println("Connected to " + this.client.getServerAddress() + " with the account " + this.client.getAccountName());
    }

}
