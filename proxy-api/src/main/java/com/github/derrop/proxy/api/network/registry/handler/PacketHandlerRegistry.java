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
package com.github.derrop.proxy.api.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface PacketHandlerRegistry {

    @Nullable <T> T handlePacketReceive(@NotNull T packet, @NotNull ProtocolDirection direction, @NotNull ProtocolState protocolState, @NotNull NetworkChannel channel);

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
