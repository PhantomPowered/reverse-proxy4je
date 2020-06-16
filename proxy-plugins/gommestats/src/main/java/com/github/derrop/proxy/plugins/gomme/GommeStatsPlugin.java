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
package com.github.derrop.proxy.plugins.gomme;

import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.command.CommandNicklist;
import com.github.derrop.proxy.plugins.gomme.command.CommandSpectatorlist;
import com.github.derrop.proxy.plugins.gomme.listener.GommeEventListener;
import com.github.derrop.proxy.plugins.gomme.match.GommeMatchListener;

@Plugin(
        id = "com.github.derrop.plugins.gommestats",
        displayName = "GommeStats",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class GommeStatsPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer container) {
        GommeStatsCore core = new GommeStatsCore(registry);

        //super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(container, new TeamParser(core));
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new GommeMatchListener(core.getMatchManager()));
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new GommeEventListener());
        registry.getProviderUnchecked(EventManager.class).registerListener(container, core.getSpectatorDetector());
        registry.getProviderUnchecked(EventManager.class).registerListener(container, core.getNickDetector());

        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandNicklist(registry, core.getNickDetector()), "nicklist");
        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandSpectatorlist(registry, core.getSpectatorDetector()), "spectatorlist", "speclist");

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        //Javalin javalin = Javalin.create(e -> e.showJavalinBanner = false).start(80);
        //javalin.get("/matches", new MatchFileHandler("/matches", core.getMatchManager()));

        Thread.currentThread().setContextClassLoader(old);
    }
}
