package com.github.derrop.proxy.util;

public final class Duo<L, R> {

    public Duo(L left, R right) {
        this.left = left;
        this.right = right;
    }

    private final L left;

    private final R right;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
