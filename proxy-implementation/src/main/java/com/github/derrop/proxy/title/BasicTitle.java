package com.github.derrop.proxy.title;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicTitle extends ProxyProvidedTitle {

    @Override
    public void send(@NotNull Player player) {
        this.send(player, this.clear, this.reset, this.times, this.subtitle, this.title);
    }

    private void send(@NotNull Player player, @Nullable Packet... packets) {
        for (Packet packet : packets) {
            if (packet == null) {
                return;
            }

            player.sendPacket(packet);
        }
    }
}
