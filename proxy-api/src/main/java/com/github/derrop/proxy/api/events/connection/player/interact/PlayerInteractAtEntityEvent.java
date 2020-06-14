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
package com.github.derrop.proxy.api.events.connection.player.interact;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.events.connection.player.PlayerEvent;
import com.github.derrop.proxy.api.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractAtEntityEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;
    private final Entity entity;
    private Vector vector;

    public PlayerInteractAtEntityEvent(@NotNull Player player, @NotNull Entity entity, @NotNull Vector vector) {
        super(player);
        this.entity = entity;
        this.vector = vector;
    }

    @NotNull
    public Vector getVector() {
        return this.vector;
    }

    public void setVector(@NotNull Vector vector) {
        this.vector = vector;
    }

    @NotNull
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

}
