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
