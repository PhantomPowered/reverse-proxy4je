/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.plugins.pathfinding;

import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.handler.Listener;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.pathfinding.provider.DefaultPathProvider;
import com.github.derrop.proxy.plugins.pathfinding.provider.PathProvider;

@Plugin(
        id = "com.github.derrop.plugins.pathfinding",
        displayName = "PathFinding",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class PathFindingPlugin {

    private ServiceRegistry serviceRegistry;

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry) {
        this.serviceRegistry = registry;

        registry.getProviderUnchecked(EventManager.class).registerListener(this);
        registry.setProvider(null, PathProvider.class, new DefaultPathProvider());
    }

    @Listener
    public void handle(PlayerLoginEvent event) {
        System.out.println("Login: " + event.getPlayer().getName());
        new Thread(() -> {
            PathProvider provider = this.serviceRegistry.getProviderUnchecked(PathProvider.class);
            provider.findRectanglePath(event.getTargetConnection().getBlockAccess(), null, new BlockPos(57, 66, 425), new BlockPos(65, 66, 418));
            //System.out.println("Found path: " + finder.findPath(event.getPlayer(), true, 0, event.getTargetConnection().getBlockAccess(), new BlockPos(-389, 66, 450), new BlockPos(-379, 66, 458)));
        }).start();
    }
}
