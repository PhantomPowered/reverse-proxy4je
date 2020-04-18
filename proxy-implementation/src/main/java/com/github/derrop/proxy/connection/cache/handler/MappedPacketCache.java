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
package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class MappedPacketCache<V extends Packet> implements PacketCacheHandler {

    private int packetId;
    private Map<Object, V> cachedPackets = new HashMap<>();
    private Function<V, Object> keyFunction;
    private Predicate<V> deleteTester;

    public MappedPacketCache(int packetId, Function<V, Object> keyFunction, Predicate<V> deleteTester) {
        this.packetId = packetId;
        this.keyFunction = keyFunction;
        this.deleteTester = deleteTester;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{packetId};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        V packet = (V) newPacket.getDeserializedPacket();

        Object key = this.keyFunction.apply(packet);

        if (this.deleteTester.test(packet)) {
            this.cachedPackets.remove(key);
        } else {
            this.cachedPackets.put(key, packet);
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (V value : this.cachedPackets.values()) {
            con.sendPacket(value);
        }
    }
}
