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
package com.github.derrop.proxy.api.util;

public class Vec4b {

    private byte b1;
    private byte b2;
    private byte b3;
    private byte b4;

    public Vec4b(byte b1, byte b2, byte b3, byte b4) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
    }

    public Vec4b(Vec4b copy) {
        this.b1 = copy.b1;
        this.b2 = copy.b2;
        this.b3 = copy.b3;
        this.b4 = copy.b4;
    }

    public byte getB1() {
        return this.b1;
    }

    public byte getB2() {
        return this.b2;
    }

    public byte getB3() {
        return this.b3;
    }

    public byte getB4() {
        return this.b4;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec4b)) {
            return false;
        } else {
            Vec4b vec4b = (Vec4b) p_equals_1_;
            return this.b1 == vec4b.b1 && (this.b4 == vec4b.b4 && (this.b2 == vec4b.b2 && this.b3 == vec4b.b3));
        }
    }

    public int hashCode() {
        int i = this.b1;
        i = 31 * i + this.b2;
        i = 31 * i + this.b3;
        i = 31 * i + this.b4;
        return i;
    }
}
