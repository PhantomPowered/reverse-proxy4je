package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerWorldBorder;
import com.github.derrop.proxy.api.connection.PacketSender;

public class WorldBorderCache implements PacketCacheHandler {

    private PacketPlayServerWorldBorder worldBorder;

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.WORLD_BORDER};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        PacketPlayServerWorldBorder border = (PacketPlayServerWorldBorder) newPacket.getDeserializedPacket();

        if (border.getAction() == PacketPlayServerWorldBorder.Action.INITIALIZE) {
            this.worldBorder = border;
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.LERP_SIZE) {
            this.worldBorder.setDiameter(border.getDiameter());
            this.worldBorder.setTargetSize(border.getTargetSize());
            this.worldBorder.setTimeUntilTarget(border.getTimeUntilTarget());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_SIZE) {
            this.worldBorder.setSize(border.getSize());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_CENTER) {
            this.worldBorder.setCenterX(border.getCenterX());
            this.worldBorder.setCenterZ(border.getCenterZ());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_WARNING_BLOCKS) {
            this.worldBorder.setWarningDistance(border.getWarningDistance());
        } else if (border.getAction() == PacketPlayServerWorldBorder.Action.SET_WARNING_TIME) {
            this.worldBorder.setWarningTime(border.getWarningTime());
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        if (this.worldBorder != null) {
            con.sendPacket(this.worldBorder);
        }
    }
}
