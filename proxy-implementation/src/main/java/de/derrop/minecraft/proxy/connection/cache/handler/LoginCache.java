package de.derrop.minecraft.proxy.connection.cache.handler;

import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.connection.cache.CachedPacket;
import de.derrop.minecraft.proxy.connection.cache.PacketCache;
import de.derrop.minecraft.proxy.connection.cache.PacketCacheHandler;
import de.derrop.minecraft.proxy.api.connection.PacketReceiver;
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
    public void sendCached(PacketReceiver con) {
        if (this.lastLogin == null) {
            Constants.EXECUTOR_SERVICE.execute(() -> {
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
            });
            return;
        }

        if (con instanceof UserConnection && ((UserConnection) con).getConnectedClient() == null) {
            // no switch, directly connected to the proxy
            Login login = new Login(this.lastLogin.getEntityId(), (short) 0, this.lastLogin.getDimension(), 0L, this.lastLogin.getDifficulty(),
                    (short) 255, this.lastLogin.getLevelType(), this.lastLogin.getViewDistance(), this.lastLogin.isReducedDebugInfo(), this.lastLogin.isNormalRespawn());

            con.sendPacket(login);
            ((UserConnection) con).setDimension(this.lastLogin.getDimension());
            return;
        }

        if (con instanceof UserConnection) {
            con.sendPacket(new EntityStatus(((UserConnection) con).getClientEntityId(), this.lastLogin.isReducedDebugInfo() ? EntityStatus.DEBUG_INFO_REDUCED : EntityStatus.DEBUG_INFO_NORMAL));

            ((UserConnection) con).setDimensionChange(true);
        }

        if (!(con instanceof UserConnection) || this.lastLogin.getDimension() == ((UserConnection) con).getDimension()) {
            con.sendPacket(new Respawn((this.lastLogin.getDimension() >= 0 ? -1 : 0), this.lastLogin.getSeed(), this.lastLogin.getDifficulty(), this.lastLogin.getGameMode(), this.lastLogin.getLevelType()));
        }

        con.sendPacket(new Respawn(this.lastLogin.getDimension(), this.lastLogin.getSeed(), this.lastLogin.getDifficulty(), this.lastLogin.getGameMode(), this.lastLogin.getLevelType()));

        if (con instanceof UserConnection) {
            ((UserConnection) con).setDimension(this.lastLogin.getDimension());
        }

    }

}
