package com.github.derrop.proxy.plugins.gommecw;

import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gommecw.command.CommandCW;
import com.github.derrop.proxy.plugins.gommecw.highlights.DefaultHighlightListener;
import com.github.derrop.proxy.plugins.gommecw.listener.GommeCWHighlightCallListener;
import com.github.derrop.proxy.plugins.gommecw.listener.GommeCWMatchListener;
import com.github.derrop.proxy.plugins.gommecw.running.RunningClanWarManager;
import com.github.derrop.proxy.plugins.gommecw.web.WebCWParser;

@Plugin(
        id = "com.github.derrop.plugins.gommecw",
        displayName = "GommeCW",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop",
        dependencies = @Dependency(id = "com.github.derrop.plugins.gommestats")
)
public class GommeCWPlugin {

    private RunningClanWarManager cwManager;
    private WebCWParser webParser;

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer container) {
        this.cwManager = new RunningClanWarManager(registry);

        this.webParser = new WebCWParser(registry);
        // TODO: proxy.registerTickable(this.webParser);

        GommeCWHighlightCallListener callListener = new GommeCWHighlightCallListener(this, new DefaultHighlightListener());
        //TODO: proxy.registerTickable(callListener);

        registry.getProviderUnchecked(EventManager.class).registerListener(container, new GommeCWMatchListener(this));
        registry.getProviderUnchecked(EventManager.class).registerListener(container, callListener);

        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandCW(this), "clanwars", "clanwar", "cw");
    }

    public WebCWParser getWebParser() {
        return this.webParser;
    }

    public RunningClanWarManager getCwManager() {
        return this.cwManager;
    }
}
