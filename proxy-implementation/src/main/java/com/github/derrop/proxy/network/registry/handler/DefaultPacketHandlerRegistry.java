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
package com.github.derrop.proxy.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.google.common.collect.ImmutableBiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultPacketHandlerRegistry implements PacketHandlerRegistry {

    private final SortedMap<Byte, Collection<PacketHandlerRegistryEntry>> entries = new TreeMap<>(Byte::compare);

    @Override
    public <T> @Nullable T handlePacketReceive(@NotNull T packet, @NotNull ProtocolDirection direction, @NotNull ProtocolState protocolState, @NotNull NetworkChannel channel) {
        for (Map.Entry<Byte, Collection<PacketHandlerRegistryEntry>> entry : ImmutableBiMap.copyOf(this.entries).entrySet()) {
            for (PacketHandlerRegistryEntry registryEntry : entry.getValue()) {
                for (PacketHandlerRegistryEntry.RegisteredEntry entryEntry : registryEntry.getEntries()) {
                    if (!(packet instanceof DecodedPacket)) {
                        if (!(packet instanceof Packet)) {
                            return null;
                        }

                        if (entryEntry.getHandledPackets().length == 0) {
                            continue;
                        }
                        boolean found = false;
                        for (int packetId : entryEntry.getHandledPackets()) {
                            if (packetId == ((Packet) packet).getId()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            continue;
                        }
                    }

                    if (entryEntry.getState() != protocolState) {
                        continue;
                    }

                    if (!packet.getClass().isAssignableFrom(entryEntry.getMethod().getParameterTypes()[1])) {
                        continue;
                    }

                    if (!entryEntry.getMethod().getParameterTypes()[0].isInstance(channel)) {
                        continue;
                    }

                    boolean found = false;
                    for (ProtocolDirection e : entryEntry.getDirections()) {
                        if (e == direction) {
                            found = true;
                            break;
                        }
                    }
                    if (entryEntry.getDirections().length != 0 && !found) {
                        continue;
                    }

                    try {
                        entryEntry.getMethod().invoke(registryEntry.getSource(), channel, packet);
                    } catch (final IllegalAccessException | InvocationTargetException ex) {
                        if (ex.getCause() instanceof CancelProceedException) {
                            return null;
                        }
                        if (ex.getCause() != null) {
                            ex.getCause().printStackTrace();
                        }
                    } catch (final CancelProceedException ex) {
                        return null;
                    }
                }
            }
        }

        return packet;
    }

    @Override
    public void registerPacketHandlerClass(@Nullable PluginContainer pluginContainer, @NotNull Object handler) {
        Map<Byte, PacketHandlerRegistryEntry> result = new HashMap<>();

        Map<Byte, Collection<PacketHandlerRegistryEntry.RegisteredEntry>> entries = this.getEntries(handler);
        for (Map.Entry<Byte, Collection<PacketHandlerRegistryEntry.RegisteredEntry>> entry : entries.entrySet()) {
            if (result.containsKey(entry.getKey())) {
                result.get(entry.getKey()).getEntries().addAll(entry.getValue());
            } else {
                result.put(entry.getKey(), new DefaultPacketHandlerRegistryEntry(pluginContainer, handler, entry.getValue()));
            }
        }

        for (Map.Entry<Byte, PacketHandlerRegistryEntry> entry : result.entrySet()) {
            this.entries.computeIfAbsent(entry.getKey(), priority -> new ArrayList<>()).add(entry.getValue());
        }
    }

    @Override
    public void unregisterPacketHandlerClass(@NotNull Object handler) {
        for (Collection<PacketHandlerRegistryEntry> value : this.entries.values()) {
            value.removeIf(e -> e.getSource() == handler);
        }
    }

    @Override
    public @NotNull Collection<PacketHandlerRegistryEntry> getRegisteredEntries() {
        return Collections.unmodifiableCollection(this.entries.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    @Override
    public void unregisterAll(@NotNull PluginContainer pluginContainer) {
        for (Collection<PacketHandlerRegistryEntry> value : this.entries.values()) {
            value.removeIf(entry -> entry.getPluginContainer() != null && entry.getPluginContainer().equals(pluginContainer));
        }
    }

    @Override
    public void unregisterAll() {
        this.entries.clear();
    }

    @NotNull
    private Map<Byte, Collection<PacketHandlerRegistryEntry.RegisteredEntry>> getEntries(@NotNull Object clazz) {
        Map<Byte, Collection<PacketHandlerRegistryEntry.RegisteredEntry>> out = new TreeMap<>(Byte::compare);
        for (Method declaredMethod : clazz.getClass().getDeclaredMethods()) {
            PacketHandler packetHandler = declaredMethod.getAnnotation(PacketHandler.class);
            if (packetHandler == null) {
                continue;
            }

            Class<?>[] parameters = declaredMethod.getParameterTypes();
            if (parameters.length != 2) {
                System.err.println("Method " + declaredMethod.getName() + " in class " + clazz.getClass().getName()
                        + " tried to register packet handler with more than two parameter or missing parameter");
                continue;
            }

            if (!NetworkChannel.class.isAssignableFrom(parameters[0])) {
                System.err.println("First argument has to be a network channel @" + declaredMethod.getName() + "->" + clazz.getClass().getName());
                continue;
            }

            if (!Packet.class.isAssignableFrom(parameters[1]) && !DecodedPacket.class.isAssignableFrom(parameters[1])) {
                System.err.println("Second argument has to be a packet or decoded packet @" + declaredMethod.getName() + "->" + clazz.getClass().getName());
                continue;
            }

            declaredMethod.setAccessible(true);

            out.computeIfAbsent(packetHandler.priority().getPriority(), priority -> new ArrayList<>()).add(new DefaultRegisteredEntry(
                    packetHandler.packetIds(),
                    declaredMethod,
                    packetHandler.protocolState(),
                    packetHandler.directions()
            ));
        }

        return out;
    }
}
