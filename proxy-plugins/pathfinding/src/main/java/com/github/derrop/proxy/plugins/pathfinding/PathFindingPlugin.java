package com.github.derrop.proxy.plugins.pathfinding;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.plugin.Plugin;
import com.github.derrop.proxy.plugins.pathfinding.provider.DefaultPathProvider;
import com.github.derrop.proxy.plugins.pathfinding.provider.PathProvider;

public class PathFindingPlugin extends Plugin {

    @Override
    public void onEnable() {
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(this);
        super.getServiceRegistry().setProvider(this, PathProvider.class, new DefaultPathProvider());
    }

    @Listener
    public void handle(PlayerLoginEvent event) {
        System.out.println("Login: " + event.getPlayer().getName());
        new Thread(() -> {
            PathProvider provider = super.getServiceRegistry().getProviderUnchecked(PathProvider.class);
            provider.findRectanglePath(event.getTargetConnection().getBlockAccess(), null, new BlockPos(57, 66, 425), new BlockPos(65, 66, 418));
            //System.out.println("Found path: " + finder.findPath(event.getPlayer(), true, 0, event.getTargetConnection().getBlockAccess(), new BlockPos(-389, 66, 450), new BlockPos(-379, 66, 458)));
        }).start();
    }

    @Override
    public void onDisable() {

    }
}
