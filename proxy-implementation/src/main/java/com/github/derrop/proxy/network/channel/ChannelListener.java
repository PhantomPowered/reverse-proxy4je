package com.github.derrop.proxy.network.channel;

import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import org.jetbrains.annotations.NotNull;

public interface ChannelListener {

    default void handleChannelActive(@NotNull NetworkChannel channel) {
    }

    default void handleChannelWriteableChange(@NotNull NetworkChannel channel) {
    }

    default void handleChannelInactive(@NotNull NetworkChannel channel) {
    }

    default void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
    }

    default void handleFinishedProceed(@NotNull Packet packet) {
    }
}
