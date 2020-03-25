package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.Respawn;

public class LoginCache implements PacketCacheHandler {

    private Login lastLogin;

    @Override
    public int[] getPacketIDs() {
        return new int[]{1}; // login packet
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        if (!(newPacket.getDeserializedPacket() instanceof Login)) {
            return;
        }
        this.lastLogin = (Login) newPacket.getDeserializedPacket();
    }

    @Override
    public void sendCached(UserConnection con) {
        if (this.lastLogin == null) {
            new Thread(() -> {
                int count = 0;
                while (this.lastLogin == null && count++ < 50) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }

                if (count >= 50) {
                    return;
                }
                this.sendCached(con);
            }).start();
            return;
        }

        if (con.getProxyClient() == null) {
            // no switch, directly connected to the proxy
            Login login = new Login(this.lastLogin.getEntityId(), (short) 0, this.lastLogin.getDimension(), 0L, this.lastLogin.getDifficulty(),
                    (short) 255, this.lastLogin.getLevelType(), this.lastLogin.getViewDistance(), this.lastLogin.isReducedDebugInfo(), this.lastLogin.isNormalRespawn());

            con.unsafe().sendPacket(login);
            con.setDimension(this.lastLogin.getDimension());
            return;
        }

        con.unsafe().sendPacket(new EntityStatus(con.getClientEntityId(), this.lastLogin.isReducedDebugInfo() ? EntityStatus.DEBUG_INFO_REDUCED : EntityStatus.DEBUG_INFO_NORMAL));

        con.setDimensionChange(true);
        if (this.lastLogin.getDimension() == con.getDimension()) {
            con.unsafe().sendPacket(new Respawn((this.lastLogin.getDimension() >= 0 ? -1 : 0), this.lastLogin.getSeed(), this.lastLogin.getDifficulty(), this.lastLogin.getGameMode(), this.lastLogin.getLevelType()));
        }

        con.unsafe().sendPacket(new Respawn(this.lastLogin.getDimension(), this.lastLogin.getSeed(), this.lastLogin.getDifficulty(), this.lastLogin.getGameMode(), this.lastLogin.getLevelType()));

        con.setDimension(this.lastLogin.getDimension());

    }
}
