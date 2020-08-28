package com.github.phantompowered.proxy.protocol.play.server.world.material;

public class PacketPlayServerEmptyMapChunk extends PacketPlayServerMapChunk {

    private static final ChunkData EMPTY_CHUNK_DATA = new ChunkData();

    static {
        EMPTY_CHUNK_DATA.dataLength = 1;
        EMPTY_CHUNK_DATA.data = new byte[256];
    }

    public PacketPlayServerEmptyMapChunk(int x, int z) {
        super(x, z, true, EMPTY_CHUNK_DATA);
    }
}
