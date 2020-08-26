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
package com.github.phantompowered.proxy.protocol.play.server.world.util;

import java.util.Objects;

public class ByteQuad {

    private final byte first;
    private final byte second;
    private final byte third;
    private final byte fourth;

    public ByteQuad(byte first, byte second, byte third, byte fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public ByteQuad copy() {
        return new ByteQuad(this.first, this.second, this.third, this.fourth);
    }

    public byte getFirst() {
        return this.first;
    }

    public byte getSecond() {
        return this.second;
    }

    public byte getThird() {
        return this.third;
    }

    public byte getFourth() {
        return this.fourth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ByteQuad byteQuad = (ByteQuad) o;
        return first == byteQuad.first && second == byteQuad.second && third == byteQuad.third && fourth == byteQuad.fourth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }
}
