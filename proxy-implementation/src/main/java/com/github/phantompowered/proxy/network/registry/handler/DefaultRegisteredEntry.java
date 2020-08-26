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
package com.github.phantompowered.proxy.network.registry.handler;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.network.registry.handler.PacketHandlerRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class DefaultRegisteredEntry implements PacketHandlerRegistryEntry.RegisteredEntry {

    private final int[] handledPackets;
    private final Method method;
    private final ProtocolState protocolState;
    private final ProtocolDirection[] directions;

    protected DefaultRegisteredEntry(int[] handledPackets, Method method, ProtocolState protocolState, ProtocolDirection[] directions) {
        this.handledPackets = handledPackets;
        this.method = method;
        this.protocolState = protocolState;
        this.directions = directions;
    }

    @Override
    public @NotNull int[] getHandledPackets() {
        return this.handledPackets;
    }

    @Override
    public @NotNull Method getMethod() {
        return this.method;
    }

    @Override
    public @NotNull ProtocolState getState() {
        return this.protocolState;
    }

    @Override
    public @Nullable ProtocolDirection[] getDirections() {
        return this.directions;
    }
}
