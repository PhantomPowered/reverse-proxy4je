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
package com.github.phantompowered.proxy.network.registry.packet;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistry;
import com.github.phantompowered.proxy.api.network.registry.packet.PacketRegistryEntry;
import com.github.phantompowered.proxy.api.network.registry.packet.exception.PacketAlreadyRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultPacketRegistry implements PacketRegistry {

    private final Collection<PacketRegistryEntry> entries = new CopyOnWriteArrayList<>();

    @Override
    public void registerPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, @NotNull Packet packet) throws PacketAlreadyRegisteredException {
        if (this.getPacket(direction, state, packet.getId()) != null) {
            throw new PacketAlreadyRegisteredException(packet);
        }

        this.entries.add(new DefaultPacketRegistryEntry(direction, state, packet));
    }

    @Override
    public @Nullable Packet getPacket(@NotNull ProtocolDirection direction, @NotNull ProtocolState state, int packetId) {
        for (PacketRegistryEntry entry : this.entries) {
            if (entry.getDirection() == direction && entry.getState() == state && entry.getPacket().getId() == packetId) {
                try {
                    return entry.getPacket().getClass().getDeclaredConstructor().newInstance();
                } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public @NotNull Collection<PacketRegistryEntry> getEntries() {
        return Collections.unmodifiableCollection(this.entries);
    }
}
