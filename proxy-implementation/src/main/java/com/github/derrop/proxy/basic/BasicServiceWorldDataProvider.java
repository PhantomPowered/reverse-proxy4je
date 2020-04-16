package com.github.derrop.proxy.basic;

import com.github.derrop.proxy.api.connection.ServiceWorldDataProvider;
import com.github.derrop.proxy.api.entity.PlayerInfo;
import com.github.derrop.proxy.api.entity.player.GameMode;
import com.github.derrop.proxy.connection.cache.handler.GameStateCache;
import com.github.derrop.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.derrop.proxy.connection.cache.handler.SimplePacketCache;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.world.PacketPlayServerTimeUpdate;

public class BasicServiceWorldDataProvider implements ServiceWorldDataProvider {

    private BasicServiceConnection connection;

    public BasicServiceWorldDataProvider(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private PacketPlayServerTimeUpdate getLastTimeUpdate() {
        return (PacketPlayServerTimeUpdate) ((SimplePacketCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler.getPacketIDs()[0] == ProtocolIds.ToClient.Play.UPDATE_TIME)).getLastPacket();
    }

    private GameStateCache getGameStateCache() {
        return (GameStateCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof GameStateCache);
    }

    @Override
    public long getWorldTime() {
        PacketPlayServerTimeUpdate update = this.getLastTimeUpdate();
        return update == null ? -1 : update.getWorldTime();
    }

    @Override
    public long getTotalWorldTime() {
        PacketPlayServerTimeUpdate update = this.getLastTimeUpdate();
        return update == null ? -1 : update.getTotalWorldTime();
    }

    @Override
    public boolean isRaining() {
        return this.getGameStateCache().isRaining();
    }

    @Override
    public boolean isThundering() {
        return this.getGameStateCache().isThundering();
    }

    @Override
    public float getRainStrength() {
        return this.getGameStateCache().getRainStrength();
    }

    @Override
    public float getThunderStrength() {
        return this.getGameStateCache().getThunderStrength();
    }

    @Override
    public GameMode getOwnGameMode() {
        return this.getGameStateCache().getGameMode();
    }

    @Override
    public PlayerInfo[] getOnlinePlayers() {
        PlayerInfoCache cache = (PlayerInfoCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof PlayerInfoCache);

        return cache.getItems().stream()
                .map(item -> new BasicPlayerInfo(item.getUuid(), item.getUsername(), item.getProperties(), GameMode.getById(item.getGamemode()), item.getPing(), item.getDisplayName()))
                .toArray(PlayerInfo[]::new);
    }
}
