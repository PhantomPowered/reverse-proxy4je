package com.github.derrop.proxy.network.registry.handler;

import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DefaultPacketHandlerRegistryEntry implements PacketHandlerRegistryEntry {

    DefaultPacketHandlerRegistryEntry(PluginContainer pluginContainer, Object source, Collection<RegisteredEntry> entries) {
        this.pluginContainer = pluginContainer;
        this.source = source;
        this.entries = entries;
    }

    private final PluginContainer pluginContainer;

    private final Object source;

    private final Collection<RegisteredEntry> entries;

    @Override
    public @Nullable PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    @Override
    public @NotNull Object getSource() {
        return this.source;
    }

    @Override
    public @NotNull Collection<RegisteredEntry> getEntries() {
        return this.entries;
    }
}
