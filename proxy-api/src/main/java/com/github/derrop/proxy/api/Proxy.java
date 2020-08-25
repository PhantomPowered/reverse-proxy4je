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
package com.github.derrop.proxy.api;

import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.tick.TickHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated // TODO: replace this shit of stupid code - it's completely unused at all
@ApiStatus.ScheduledForRemoval
public abstract class Proxy {

    /**
     * @deprecated Instead of using {@link Proxy#getServiceRegistry()} this will be given to every class which needs it
     */
    @NotNull
    @Deprecated
    @ApiStatus.Internal
    @ApiStatus.ScheduledForRemoval
    public abstract ServiceRegistry getServiceRegistry();

    /**
     * @deprecated Will be removed in a further update - and is only for internal use
     */
    @Deprecated
    @ApiStatus.Internal
    @ApiStatus.ScheduledForRemoval
    public abstract void shutdown();

    /**
     * @deprecated Will be moved to the service registry using {@link com.github.derrop.proxy.api.tick.TickHandlerProvider}
     */
    @Deprecated
    public abstract void registerTickable(@NotNull TickHandler tickHandler);
}
