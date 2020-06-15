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
package com.github.derrop.proxy.api.entity.types;

import com.github.derrop.proxy.api.connection.player.inventory.EquipmentSlot;
import com.github.derrop.proxy.api.entity.types.living.EntityLiving;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public interface ArmorStand extends EntityLiving {

    @NotNull
    ItemStack getItem(@NotNull EquipmentSlot slot);

    @NotNull
    EulerAngle getBodyPosition(@NotNull BodyPosition position);

    boolean hasBasePlate();

    boolean hasGravity();

    boolean hasArms();

    boolean isSmall();

    boolean isMarker();

    enum BodyPosition {

        ARM_RIGHT,
        ARM_LEFT,
        LEG_RIGHT,
        LEG_LEFT,
        HEAD,
        BODY
    }
}
