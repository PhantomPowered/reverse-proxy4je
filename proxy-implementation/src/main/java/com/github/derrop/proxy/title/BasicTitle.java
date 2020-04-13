package com.github.derrop.proxy.title;

import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicTitle extends ProxyProvidedTitle {

    @Override
    public void send(@NotNull ProxiedPlayer player) {
        this.send(player, this.clear, this.reset, this.times, this.subtitle, this.title);
    }

    private void send(@NotNull ProxiedPlayer proxiedPlayer, @Nullable Packet... packets) {
        for (Packet packet : packets) {
            if (packet == null) {
                return;
            }

            proxiedPlayer.sendPacket(packet);
        }
    }
}
