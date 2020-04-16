package com.github.derrop.proxy.util;

public final class MathHelper {

    private MathHelper() {
        throw new UnsupportedOperationException();
    }

    public static double floor(double d1, double d2, double d3) {
        if (d1 < d2) {
            return d2;
        } else {
            return Math.min(d1, d3);
        }
    }
}
