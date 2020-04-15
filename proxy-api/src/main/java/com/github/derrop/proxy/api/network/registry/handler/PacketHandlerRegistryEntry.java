package com.github.derrop.proxy.api.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

public interface PacketHandlerRegistryEntry {

    @Nullable Plugin getPlugin();

    @NotNull Object getSource();

    @NotNull Collection<RegisteredEntry> getEntries();

    interface RegisteredEntry {

        @NotNull int[] getHandledPackets();

        @NotNull Method getMethod();

        @NotNull ProtocolState getState();

    }
}
