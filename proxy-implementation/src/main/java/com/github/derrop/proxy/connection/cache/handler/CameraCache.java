package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.api.connection.PacketSender;
import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.entity.player.PacketPlayServerCamera;

public class CameraCache implements PacketCacheHandler {

    private PacketPlayServerCamera camera;

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.CAMERA};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        this.camera = (PacketPlayServerCamera) newPacket.getDeserializedPacket();
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.camera == null) {
            return;
        }
        Constants.EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            con.sendPacket(camera);
        });
    }
}
