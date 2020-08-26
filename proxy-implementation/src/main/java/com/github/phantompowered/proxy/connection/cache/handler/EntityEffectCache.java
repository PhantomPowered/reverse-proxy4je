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
package com.github.phantompowered.proxy.connection.cache.handler;

import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.PacketSender;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.cache.PacketCache;
import com.github.phantompowered.proxy.connection.cache.PacketCacheHandler;
import com.github.phantompowered.proxy.connection.cache.TimedEntityEffect;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityDestroy;
import com.github.phantompowered.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;
import com.github.phantompowered.proxy.protocol.play.server.entity.effect.PacketPlayServerRemoveEntityEffect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEffectCache implements PacketCacheHandler {

    private final Map<Integer, Map<Byte, TimedEntityEffect>> effects = new ConcurrentHashMap<>();

    @Override
    public int[] getPacketIDs() {
        return new int[]{
                ProtocolIds.ToClient.Play.ENTITY_EFFECT,
                ProtocolIds.ToClient.Play.REMOVE_ENTITY_EFFECT,
                ProtocolIds.ToClient.Play.ENTITY_DESTROY
        };
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        if (newPacket instanceof PacketPlayServerRemoveEntityEffect) {
            PacketPlayServerRemoveEntityEffect effect = (PacketPlayServerRemoveEntityEffect) newPacket;
            if (this.effects.containsKey(effect.getEntityId())) {
                Map<Byte, TimedEntityEffect> effects = this.effects.get(effect.getEntityId());
                effects.remove((byte) effect.getEffectId());
                if (effects.isEmpty()) {
                    this.effects.remove(effect.getEntityId());
                }
            }
        } else if (newPacket instanceof PacketPlayServerEntityEffect) {
            TimedEntityEffect effect = TimedEntityEffect.fromEntityEffect((PacketPlayServerEntityEffect) newPacket);

            if (!this.effects.containsKey(effect.getEntityId())) {
                this.effects.put(effect.getEntityId(), new ConcurrentHashMap<>());
            }

            this.effects.get(effect.getEntityId()).put(effect.getEffectId(), effect);
        } else if (newPacket instanceof PacketPlayServerEntityDestroy) {
            PacketPlayServerEntityDestroy destroy = (PacketPlayServerEntityDestroy) newPacket;
            for (int entityId : destroy.getEntityIds()) {
                this.effects.remove(entityId);
            }
        }
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        for (Map<Byte, TimedEntityEffect> effects : this.effects.values()) {
            for (TimedEntityEffect effect : effects.values()) {
                PacketPlayServerEntityEffect effectPacket = effect.toEntityEffect();
                if (effectPacket != null) {
                    con.sendPacket(effectPacket);
                } else {
                    effects.remove(effect.getEffectId());
                    if (effects.isEmpty()) {
                        this.effects.remove(effect.getEntityId());
                    }
                }
            }
        }
    }

    public Map<Byte, TimedEntityEffect> getEffects(int entityId) {
        Map<Byte, TimedEntityEffect> effects = this.effects.get(entityId);
        if (effects == null) {
            effects = new ConcurrentHashMap<>();
            this.effects.put(entityId, effects);
            return effects;
        }

        for (TimedEntityEffect effect : effects.values()) {
            if (!effect.isValid()) {
                effects.remove(effect.getEffectId());
            }
        }

        return effects;
    }

    public boolean removeEffect(int entityId, byte effectId) {
        Map<Byte, TimedEntityEffect> effects = this.effects.get(entityId);
        if (effects != null) {
            if (effects.remove(effectId) != null) {
                if (effects.isEmpty()) {
                    this.effects.remove(entityId);
                }
                return true;
            }
        }
        return false;
    }

}
