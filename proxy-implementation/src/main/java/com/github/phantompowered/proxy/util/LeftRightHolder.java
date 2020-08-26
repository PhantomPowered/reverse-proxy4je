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
package com.github.phantompowered.proxy.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class LeftRightHolder<L, R> implements Map.Entry<L, R> {

    private final L left;
    private final R right;

    private LeftRightHolder(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> @NotNull LeftRightHolder<L, R> left(L left) {
        return of(left, null);
    }

    public static <L, R> @NotNull LeftRightHolder<L, R> right(R right) {
        return of(null, right);
    }

    public static <L, R> @NotNull LeftRightHolder<L, R> of(L left, R right) {
        return new LeftRightHolder<>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public L getKey() {
        return this.left;
    }

    @Override
    public R getValue() {
        return this.right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }
}
