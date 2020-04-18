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
package com.github.derrop.proxy.connection.cache;

import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;

public class TimedEntityEffect {

    private int entityId;
    private byte effectId;
    private byte amplifier;
    private long timeout;
    private byte hideParticles;

    private TimedEntityEffect(int entityId, byte effectId, byte amplifier, long timeout, byte hideParticles) {
        this.entityId = entityId;
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.timeout = timeout;
        this.hideParticles = hideParticles;
    }

    public int getEntityId() {
        return entityId;
    }

    public byte getEffectId() {
        return effectId;
    }

    public byte getAmplifier() {
        return amplifier;
    }

    public long getTimeout() {
        return timeout;
    }

    public byte getHideParticles() {
        return hideParticles;
    }

    public static TimedEntityEffect fromEntityEffect(PacketPlayServerEntityEffect entityEffect) {
        long durationMillis = entityEffect.getDuration() >= 32767 ? -1 : ((entityEffect.getDuration() / 20) * 1000) + System.currentTimeMillis();

        return new TimedEntityEffect(
                entityEffect.getEntityId(), entityEffect.getEffectId(),
                entityEffect.getAmplifier(), durationMillis,
                entityEffect.getHideParticles()
        );
    }

    public PacketPlayServerEntityEffect toEntityEffect() {
        if (System.currentTimeMillis() >= this.timeout) {
            return null;
        }

        int durationTicks = this.timeout == -1 ? 32767 : (int) (((this.timeout - System.currentTimeMillis()) / 1000) * 20);
        return new PacketPlayServerEntityEffect(this.entityId, this.effectId, this.amplifier, durationTicks, this.hideParticles);
    }

}
