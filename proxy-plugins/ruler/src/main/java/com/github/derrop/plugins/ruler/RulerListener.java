package com.github.derrop.plugins.ruler;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.player.PlayerInteractEvent;
import com.github.derrop.proxy.api.location.BlockPos;
import net.kyori.text.TextComponent;

public class RulerListener {

    @Listener
    public void handleInteract(PlayerInteractEvent event) {
        if (event.getType() != PlayerInteractEvent.Type.LEFT_CLICK) {
            return;
        }

        ServiceConnection connection = event.getPlayer().getConnectedClient();
        if (connection == null) {
            return;
        }

        if (!event.getPlayer().getConnectedClient().isSneaking()) {
            return;
        }

        BlockPos pos = connection.getTargetBlock(100);
        if (pos == null) {
            return;
        }

        event.getPlayer().sendActionBar(1, TextComponent.of("§7Range to §e" + pos.toShortString() + "§7: §e" + String.format("%.3f", connection.getLocation().distance(pos.toLocation())) + " blocks"));
    }

}
