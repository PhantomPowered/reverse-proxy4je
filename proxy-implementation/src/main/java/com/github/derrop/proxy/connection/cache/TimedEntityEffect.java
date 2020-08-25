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

import com.github.derrop.proxy.api.entity.EntityEffect;
import com.github.derrop.proxy.protocol.play.server.entity.effect.PacketPlayServerEntityEffect;

public class TimedEntityEffect implements EntityEffect {

    private final int entityId;
    private final byte effectId;
    private final byte amplifier;
    private final int initialDurationTicks;
    private final long timeout;
    private final boolean hideParticles;

    public TimedEntityEffect(int entityId, byte effectId, byte amplifier, int initialDurationTicks, long timeout, boolean hideParticles) {
        this.entityId = entityId;
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.initialDurationTicks = initialDurationTicks;
        this.timeout = timeout;
        this.hideParticles = hideParticles;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public byte getEffectId() {
        return this.effectId;
    }

    @Override
    public byte getAmplifier() {
        return this.amplifier;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public boolean isInfinite() {
        return this.timeout == -1;
    }

    @Override
    public boolean isHidingParticles() {
        return this.hideParticles;
    }

    @Override
    public int getInitialDurationTicks() {
        return this.initialDurationTicks;
    }

    @Override
    public boolean isValid() {
        return this.timeout == -1 || this.timeout >= System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "TimedEntityEffect{"
                + "entityId=" + entityId
                + ", effectId=" + effectId
                + ", amplifier=" + amplifier
                + ", initialDurationTicks=" + initialDurationTicks
                + ", timeout=" + timeout
                + ", hideParticles=" + hideParticles
                + '}';
    }

    private static long getTimeout(int ticks) {
        return ticks <= 0 || ticks >= 32767 ? -1 : ((ticks / 20) * 1000) + System.currentTimeMillis();
    }

    public static TimedEntityEffect fromEntityEffect(PacketPlayServerEntityEffect entityEffect) {
        return create(entityEffect.getEntityId(), entityEffect.getEffectId(), entityEffect.getAmplifier(), entityEffect.getDuration(), entityEffect.isHidingParticles());
    }

    public static TimedEntityEffect create(int entityId, byte effectId, byte amplifier, int duration, boolean hideParticles) {
        return new TimedEntityEffect(
                entityId, effectId,
                amplifier,
                duration, getTimeout(duration),
                hideParticles
        );
    }

    public PacketPlayServerEntityEffect toEntityEffect() {
        if (!this.isValid()) {
            return null;
        }

        int durationTicks = this.timeout == -1 ? 32767 : (int) (((this.timeout - System.currentTimeMillis()) / 1000) * 20);
        return new PacketPlayServerEntityEffect(this.entityId, this.effectId, this.amplifier, durationTicks, this.hideParticles);
    }

}
