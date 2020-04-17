package com.github.derrop.proxy.api.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.util.Identifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface PacketHandlerRegistry {

    @Nullable <T extends Identifiable> T handlePacketReceive(@NotNull T packet, @NotNull ProtocolDirection direction, @NotNull ProtocolState protocolState, @NotNull NetworkChannel channel);

    default void registerPacketHandlerClass(@Nullable PluginContainer pluginContainer, @NotNull Class<?> clazz) {
        try {
            this.registerPacketHandlerClass(pluginContainer, clazz.getDeclaredConstructor().newInstance());
        } catch (final NoSuchMethodException ex) {
            System.err.println("Missing constructor without arguments in packet handler class " + clazz.getName());
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            System.err.println("Unable to instantiate constructor of class " + clazz.getName());
            ex.printStackTrace();
        }
    }

    void registerPacketHandlerClass(@Nullable PluginContainer pluginContainer, @NotNull Object handler);

    void unregisterPacketHandlerClass(@NotNull Object handler);

    @NotNull Collection<PacketHandlerRegistryEntry> getRegisteredEntries();

    void unregisterAll(@NotNull PluginContainer pluginContainer);

    void unregisterAll();
}
