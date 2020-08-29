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
package com.github.phantompowered.proxy.api.connection;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServiceConnectResult {

    private final boolean success;
    private final Component failureReason;

    private ServiceConnectResult(boolean success, Component failureReason) {
        this.success = success;
        this.failureReason = failureReason;
    }

    @NotNull
    public static ServiceConnectResult success() {
        return new ServiceConnectResult(true, null);
    }

    @NotNull
    public static ServiceConnectResult unknownFailure() {
        return failure(null);
    }

    @NotNull
    public static ServiceConnectResult failure(Component component) {
        return new ServiceConnectResult(false, component);
    }

    public boolean isSuccess() {
        return this.success;
    }

    @Nullable
    public Component getFailureReason() {
        Preconditions.checkArgument(!this.success, "failure reason is only available for failed results");
        return this.failureReason;
    }
}
