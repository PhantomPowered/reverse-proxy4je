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
package com.github.phantompowered.proxy.api.events.connection.service.inventory;

import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.events.connection.service.ServiceConnectionEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ServiceInventoryOpenEvent extends ServiceConnectionEvent {

    private final Component title;
    private final String inventoryType;
    private final int slotCount;

    public ServiceInventoryOpenEvent(@NotNull ServiceConnection connection, Component title, String inventoryType, int slotCount) {
        super(connection);
        this.title = title;
        this.inventoryType = inventoryType;
        this.slotCount = slotCount;
    }

    public Component getTitle() {
        return this.title;
    }

    public String getInventoryType() {
        return this.inventoryType;
    }

    public int getSlotCount() {
        return this.slotCount;
    }
}
