package net.md_5.bungee.protocol;

import com.github.derrop.proxy.connection.cache.packet.system.Disconnect;
import com.github.derrop.proxy.protocol.Handshake;
import com.github.derrop.proxy.protocol.legacy.PacketLegacyHandshake;
import com.github.derrop.proxy.protocol.legacy.PacketLegacyPing;
import com.github.derrop.proxy.protocol.login.*;
import com.github.derrop.proxy.protocol.play.client.*;
import com.github.derrop.proxy.protocol.play.server.*;
import com.github.derrop.proxy.protocol.play.shared.*;
import com.github.derrop.proxy.protocol.status.PacketStatusPing;
import com.github.derrop.proxy.protocol.status.PacketStatusRequest;
import com.github.derrop.proxy.protocol.status.PacketStatusResponse;

public abstract class AbstractPacketHandler {

    public void handle(Disconnect disconnect) throws Exception {
    }

    public void handle(PacketLegacyPing ping) throws Exception {
    }

    public void handle(PacketPlayServerTabCompleteResponse tabResponse) throws Exception {
    }

    public void handle(PacketStatusPing ping) throws Exception {
    }

    public void handle(PacketStatusRequest statusRequest) throws Exception {
    }

    public void handle(PacketStatusResponse statusResponse) throws Exception {
    }

    public void handle(Handshake handshake) throws Exception {
    }

    public void handle(PacketPlayKeepAlive keepAlive) throws Exception {
    }

    public void handle(PacketPlayServerLogin login) throws Exception {
    }

    public void handle(PacketPlayChatMessage chat) throws Exception {
    }

    public void handle(PacketPlayServerRespawn respawn) throws Exception {
    }

    public void handle(PacketLoginLoginRequest loginRequest) throws Exception {
    }

    public void handle(PacketPlayClientSettings settings) throws Exception {
    }

    public void handle(PacketPlayServerPlayerListItem playerListItem) throws Exception {
    }

    public void handle(PacketPlayServerPlayerListHeaderFooter playerListHeaderFooter) throws Exception {
    }

    public void handle(PacketPlayClientTabCompleteRequest tabComplete) throws Exception {
    }

    public void handle(PacketPlayServerScoreboardObjective scoreboardObjective) throws Exception {
    }

    public void handle(PacketPlayServerScoreboardScore scoreboardScore) throws Exception {
    }

    public void handle(PacketLoginEncryptionRequest encryptionRequest) throws Exception {
    }

    public void handle(PacketPlayServerScoreboardDisplay displayScoreboard) throws Exception {
    }

    public void handle(PacketPlayServerScoreboardTeam team) throws Exception {
    }

    public void handle(PacketPlayServerTitle title) throws Exception {
    }

    public void handle(PacketPlayPluginMessage pluginMessage) throws Exception {
    }

    public void handle(PacketPlayKickPlayer kick) throws Exception {
    }

    public void handle(PacketLoginEncryptionResponse encryptionResponse) throws Exception {
    }

    public void handle(PacketPlayServerLoginSuccess loginSuccess) throws Exception {
    }

    public void handle(PacketLegacyHandshake legacyHandshake) throws Exception {
    }

    public void handle(PacketLoginSetCompression setCompression) throws Exception {
    }

    public void handle(PacketPlayServerEntityStatus status) throws Exception {
    }
}
