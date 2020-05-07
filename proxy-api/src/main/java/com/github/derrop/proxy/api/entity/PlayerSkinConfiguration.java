package com.github.derrop.proxy.api.entity;

public interface PlayerSkinConfiguration {

    SkinPart[] getEnabledParts();

    boolean has(SkinPart part);

    boolean hasCape();

    boolean hasJacket();

    boolean hasLeftSleeve();

    boolean hasRightSleeve();

    boolean hasLeftPants();

    boolean hasRightPants();

    boolean hasHat();

    enum SkinPart {

        CAPE,
        JACKET,
        LEFT_SLEEVE,
        RIGHT_SLEEVE,
        LEFT_PANTS,
        RIGHT_PANTS,
        HAT;

        private final int mask;

        SkinPart() {
            this.mask = 1 << super.ordinal();
        }

        public int getMask() {
            return this.mask;
        }

        public int getPartId() {
            return super.ordinal();
        }

    }

}
