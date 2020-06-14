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
package com.github.derrop.proxy.api.events.connection.player;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.event.Cancelable;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractEvent extends PlayerEvent implements Cancelable {

    private boolean cancel;
    private final Action action;

    public PlayerInteractEvent(@NotNull Player player, @NotNull PlayerInteractEvent.Action action) {
        super(player);
        this.action = action;
    }

    @NotNull
    public PlayerInteractEvent.Action getAction() {
        return this.action;
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    public static enum Action {
        LEFT_CLICK_BLOCK(true), RIGHT_CLICK_BLOCK(false), LEFT_CLICK_AIR(true), RIGHT_CLICK_AIR(false);

        private final boolean left;

        Action(boolean left) {
            this.left = left;
        }

        public boolean isLeftClick() {
            return this.left;
        }

        public boolean isRightClick() {
            return !this.isLeftClick();
        }

    }

}
