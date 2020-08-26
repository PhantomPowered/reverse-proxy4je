package com.github.phantompowered.proxy.protocol.play.server.world.material;

public class PacketPlayServerEmptyMapChunk extends PacketPlayServerMapChunk {

    private static final Extracted EMPTY_EXTRACTED = new Extracted();

    static {
        EMPTY_EXTRACTED.dataLength = 1;
        EMPTY_EXTRACTED.data = new byte[256];
    }

    public PacketPlayServerEmptyMapChunk(int x, int z) {
        super(x, z, true, EMPTY_EXTRACTED);
    }
}
