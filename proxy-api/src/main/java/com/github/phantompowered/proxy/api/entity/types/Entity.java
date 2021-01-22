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
package com.github.phantompowered.proxy.api.entity.types;

import com.github.phantompowered.proxy.api.entity.EntityType;
import com.github.phantompowered.proxy.api.entity.LivingEntityType;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Entity extends Scaleable {

    boolean isBurning();

    boolean isSneaking();

    boolean isRiding();

    boolean isSprinting();

    boolean isEating();

    boolean isInvisible();

    /**
     * Returns true if the flag is active for this entity.
     * Flags:
     * - 0 = is burning
     * - 1 = is sneaking
     * - 2 = is riding
     * - 3 = is sprinting
     * - 4 = is eating
     * - 5 = is invisible
     */
    boolean getFlag(int flag);

    short getAirTicks();

    boolean isCustomNameVisible();

    boolean isSilent();

    boolean hasCustomName();

    String getCustomName();

    boolean isLiving();

    @Nullable
    EntityType getType();

    @Nullable
    LivingEntityType getLivingType();

    boolean isOfType(@Nullable EntityType type);

    boolean isOfType(@Nullable LivingEntityType type);

    @NotNull
    Location getLocation();

    void teleport(@NotNull Location location);

    boolean isOnGround();

    int getEntityId();

    int getDimension();

    @NotNull
    Unsafe unsafe();

    @NotNull
    Callable getCallable();

    float getHeadHeight();

    Object getProperty(String key);

    void removeProperty(String key);

    void setProperty(String key, Object value);

    interface Unsafe {

        void setLocationUnchecked(@NotNull Location locationUnchecked);

    }

    interface Callable {

        void handleEntityPacket(@NotNull Packet packet);
    }
}
