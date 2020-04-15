package com.github.derrop.proxy.network.registry.handler;

import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DefaultPacketHandlerRegistryEntry implements PacketHandlerRegistryEntry {

    DefaultPacketHandlerRegistryEntry(Plugin plugin, Object source, Collection<RegisteredEntry> entries) {
        this.plugin = plugin;
        this.source = source;
        this.entries = entries;
    }

    private final Plugin plugin;

    private final Object source;

    private final Collection<RegisteredEntry> entries;

    @Override
    public @Nullable Plugin getPlugin() {
        return this.plugin;
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
