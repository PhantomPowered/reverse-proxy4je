package com.github.derrop.proxy.network.listener;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.network.channel.ChannelListener;
import net.md_5.bungee.Util;
import org.jetbrains.annotations.NotNull;

public class InitialListener implements ChannelListener {

    @Override
    public void handleChannelActive(@NotNull NetworkChannel channel) {
        System.out.println("InitialHandler connected -> " + channel.getAddress());
        channel.setProperty(InitialHandler.INIT_STATE, InitialHandler.State.HANDSHAKE);
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (InitialHandler.canSendKickMessage(channel)) {
            InitialHandler.disconnect(channel, ChatColor.RED + Util.exception(cause));
        } else {
            channel.close();
        }
    }

}
