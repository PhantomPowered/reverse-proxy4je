package com.github.derrop.proxy.plugins;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.plugin.Plugin;
import com.github.derrop.proxy.api.util.BlockPos;
import com.github.derrop.proxy.plugins.test.AStarPathFinder;

public class PathFindingPlugin extends Plugin {

    @Override
    public void onEnable() {
        super.getServiceRegistry().getProviderUnchecked(Proxy.class).getEventManager().registerListener(this);
    }

    @Listener
    public void handle(PlayerLoginEvent event) {
        System.out.println("Login: " + event.getPlayer().getName());
        new Thread(() -> {
            AStarPathFinder finder = new AStarPathFinder();
            System.out.println("Found path: " + finder.findPath(event.getPlayer(), true, 0, event.getTargetConnection().getBlockAccess(), new BlockPos(-389, 66, 450), new BlockPos(-379, 66, 458)));
        }).start();
    }

    @Override
    public void onDisable() {

    }
}
