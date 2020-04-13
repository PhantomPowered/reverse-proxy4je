package com.github.derrop.proxy.util;

public class PlayerPositionPacketUtil {

    public static byte getFixRotation(float in) {
        return (byte) (in * 256F / 360F);
    }

    public static int getFixLocation(double in) {
        return floor(in * 32D);
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

}
