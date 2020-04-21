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

import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.cache.TimedEntityEffect;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerRemoveEntityEffect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEffectCache implements PacketCacheHandler {

    private Map<Integer, Map<Integer, TimedEntityEffect>> effects = new ConcurrentHashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{ProtocolIds.ToClient.Play.ENTITY_EFFECT, ProtocolIds.ToClient.Play.REMOVE_ENTITY_EFFECT};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        if (newPacket.getDeserializedPacket() instanceof PacketPlayServerRemoveEntityEffect) {
            PacketPlayServerRemoveEntityEffect effect = (PacketPlayServerRemoveEntityEffect) newPacket.getDeserializedPacket();
            if (this.effects.containsKey(effect.getEntityId())) {
                Map<Integer, TimedEntityEffect> effects = this.effects.get(effect.getEntityId());
                effects.remove(effect.getEffectId());
                if (effects.isEmpty()) {
                    this.effects.remove(effect.getEntityId());
                }
            }
        } else if (newPacket.getDeserializedPacket() instanceof PacketPlayServerEntityEffect) {
            TimedEntityEffect effect = TimedEntityEffect.fromEntityEffect((PacketPlayServerEntityEffect) newPacket.getDeserializedPacket());

            if (!this.effects.containsKey(effect.getEntityId())) {
                this.effects.put(effect.getEntityId(), new ConcurrentHashMap<>());
            }

            this.effects.get(effect.getEntityId()).put((int) effect.getEffectId(), effect);
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (Map<Integer, TimedEntityEffect> effects : this.effects.values()) {
            for (TimedEntityEffect effect : effects.values()) {
                PacketPlayServerEntityEffect effectPacket = effect.toEntityEffect();
                if (effectPacket != null) {
                    con.sendPacket(effectPacket);
                } else {
                    effects.remove((int) effect.getEffectId());
                    if (effects.isEmpty()) {
                        this.effects.remove(effect.getEntityId());
                    }
                }
            }
        }
    }
}
