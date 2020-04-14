package com.github.derrop.proxy.api.block;

public interface BlockConsumer {

    void accept(int x, int y, int z, int oldState, int state);

}
