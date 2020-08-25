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
package com.github.derrop.proxy.tick;

import com.github.derrop.proxy.api.APIUtil;
import com.github.derrop.proxy.api.tick.TickHandler;
import com.github.derrop.proxy.api.tick.TickHandlerProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class DefaultTickHandlerProvider implements TickHandlerProvider {

    private final Collection<TickHandler> tickHandlers = new CopyOnWriteArrayList<>();

    @ApiStatus.Internal
    public void startMainLoop() {
        APIUtil.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::runTick, 50, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void registerHandler(TickHandler handler) {
        this.tickHandlers.add(handler);
    }

    @Override
    public void unregisterHandler(TickHandler handler) {
        this.tickHandlers.remove(handler);
    }

    @Override
    public void runTick() {
        for (TickHandler tickHandler : this.tickHandlers) {
            try {
                tickHandler.handleTick();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
