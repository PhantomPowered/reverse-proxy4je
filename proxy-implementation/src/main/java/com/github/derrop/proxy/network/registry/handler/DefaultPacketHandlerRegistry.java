package com.github.derrop.proxy.network.registry.handler;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistry;
import com.github.derrop.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import com.github.derrop.proxy.api.plugin.Plugin;
import com.github.derrop.proxy.api.util.Identifiable;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultPacketHandlerRegistry implements PacketHandlerRegistry {

    private final BiMap<Byte, PacketHandlerRegistryEntry> entries = HashBiMap.create();

    @Override
    public <T extends Identifiable> @Nullable T handlePacketReceive(@NotNull T packet, @NotNull ProtocolState protocolState, @NotNull NetworkChannel channel) {
        SortedMap<Byte, PacketHandlerRegistryEntry> sorted = new TreeMap<>(Byte::compare);
        this.entries.forEach((k, v) -> sorted.put(k, v));

        for (Map.Entry<Byte, PacketHandlerRegistryEntry> entry : ImmutableBiMap.copyOf(sorted).entrySet()) {
            for (PacketHandlerRegistryEntry.RegisteredEntry entryEntry : entry.getValue().getEntries()) {
                if (entryEntry.getHandledPackets().length == 0 || Arrays.stream(entryEntry.getHandledPackets()).noneMatch(e -> e == packet.getId())) {
                    continue;
                }

                if (entryEntry.getState() != protocolState) {
                    continue;
                }

                if (!packet.getClass().isAssignableFrom(entryEntry.getMethod().getParameterTypes()[1])) {
                    continue;
                }

                try {
                    entryEntry.getMethod().invoke(entry.getValue().getSource(), channel, packet);
                } catch (final IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                } catch (final CancelProceedException ex) {
                    return null;
                }
            }
        }

        return packet;
    }

    @Override
    public void registerPacketHandlerClass(@Nullable Plugin plugin, @NotNull Object handler) {
        Map<Byte, PacketHandlerRegistryEntry.RegisteredEntry> entries = this.getEntries(handler);
        for (Map.Entry<Byte, PacketHandlerRegistryEntry.RegisteredEntry> byteRegisteredEntryEntry : entries.entrySet()) {
            this.entries.put(byteRegisteredEntryEntry.getKey(), new DefaultPacketHandlerRegistryEntry(plugin, handler, entries.values()));
        }
    }

    @Override
    public void unregisterPacketHandlerClass(@NotNull Object handler) {
        this.entries.values().removeIf(e -> e.getSource() == handler);
    }

    @Override
    public @NotNull Collection<PacketHandlerRegistryEntry> getRegisteredEntries() {
        return Collections.unmodifiableCollection(this.entries.values());
    }

    @Override
    public void unregisterAll(@NotNull Plugin plugin) {
        this.entries.values().removeIf(entry -> entry.getPlugin() != null && entry.getPlugin().equals(plugin));
    }

    @Override
    public void unregisterAll() {
        this.entries.clear();
    }

    @NotNull
    private Map<Byte, PacketHandlerRegistryEntry.RegisteredEntry> getEntries(@NotNull Object clazz) {
        Map<Byte, PacketHandlerRegistryEntry.RegisteredEntry> out = new TreeMap<>(Byte::compare);
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

            out.put(packetHandler.priority().getPriority(), new DefaultRegisteredEntry(packetHandler.packetIds(), declaredMethod, packetHandler.protocolState()));
        }

        return out;
    }
}
