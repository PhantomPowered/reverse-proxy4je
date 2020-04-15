package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.event.priority.EventPriority;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.minecraft.CryptManager;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.cipher.PacketCipherDecoder;
import com.github.derrop.proxy.network.cipher.PacketCipherEncoder;
import com.github.derrop.proxy.network.handler.HandlerEndpoint;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.login.PacketLoginEncryptionRequest;
import com.github.derrop.proxy.protocol.login.PacketLoginEncryptionResponse;
import com.github.derrop.proxy.protocol.login.PacketLoginSetCompression;
import com.github.derrop.proxy.protocol.login.PacketPlayServerLoginSuccess;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.PacketWrapper;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PublicKey;

public class ProxyClientLoginHandler extends PacketHandler {
    private final ConnectedProxyClient proxyClient;
    private final BasicServiceConnection connection;

    public ProxyClientLoginHandler(ConnectedProxyClient proxyClient, BasicServiceConnection connection) {
        this.proxyClient = proxyClient;
        this.connection = connection;
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        if (this.connection != null) {
            this.proxyClient.getProxy().unregisterConnection(this.connection);
        }
    }

    @Override
    public void exception(Throwable t) throws Exception {
        t.printStackTrace();
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        System.out.println("Connected to " + this.proxyClient.getAddress() + " with the account " + this.proxyClient.getAccountName());
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return true;
    }

    @Override
    public void handle(@NotNull Packet packet) throws Exception {
        if (packet instanceof PacketPlayServerKickPlayer) {
            this.handle((PacketPlayServerKickPlayer) packet);
            return;
        }

        if (packet instanceof PacketLoginEncryptionRequest) {
            this.handle((PacketLoginEncryptionRequest) packet);
            return;
        }

        if (packet instanceof PacketPlayServerLoginSuccess) {
            this.handle((PacketPlayServerLoginSuccess) packet);
            return;
        }

        if (packet instanceof )
    }

    private void handle(PacketPlayServerKickPlayer kick) throws Exception {
        if (this.proxyClient == null) {
            return;
        }
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        this.proxyClient.setLastKickReason(reason);
        this.proxyClient.connectionFailed();
    }

    private void handle(PacketLoginEncryptionRequest request) throws Exception {
        if (this.proxyClient.getCredentials().isOffline()) {
            throw new IllegalStateException("Joined with an offline account on an online mode server");
        }

        final SecretKey secretKey = CryptManager.createNewSharedKey();
        String s = request.getServerId();
        PublicKey publicKey = CryptManager.decodePublicKey(request.getPublicKey());
        String s1 = (new BigInteger(CryptManager.getServerIdHash(s, publicKey, secretKey))).toString(16);

        try {
            this.proxyClient.getSessionService().joinServer(this.proxyClient.getAuthentication().getSelectedProfile(), this.proxyClient.getAuthentication().getAuthenticatedToken(), s1);
        } catch (AuthenticationException exception) {
            throw new Error("Failed to join server on auth servers!", exception);
        }

        byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretKey.getEncoded());
        byte[] verifyTokenEncrypted = CryptManager.encryptData(publicKey, request.getVerifyToken());
        this.proxyClient.getChannelWrapper().write(new PacketLoginEncryptionResponse(secretKeyEncrypted, verifyTokenEncrypted));

        this.proxyClient.getChannelWrapper().getHandle().pipeline().addBefore(
                NetworkUtils.LENGTH_DECODER,
                NetworkUtils.DECRYPT,
                new PacketCipherDecoder(secretKey)
        );
        this.proxyClient.getChannelWrapper().getHandle().pipeline().addBefore(
                NetworkUtils.LENGTH_ENCODER,
                NetworkUtils.ENCRYPT,
                new PacketCipherEncoder(secretKey)
        );
    }
    @com.github.derrop.proxy.api.network.PacketHandler(packetIds = {ProtocolIds.ClientBound.Login.SUCCESS}, protocolState = ProtocolState.LOGIN, priority = EventPriority.FIRST)
    private void handle(NetworkChannel channel, PacketPlayServerLoginSuccess loginSuccess) throws Exception {
        channel.setProtocolState(ProtocolState.PLAY);
        channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setHandler(new DownstreamBridge(this.connection));
        throw CancelProceedException.INSTANCE; // without this, the LoginSuccess would be recorded by ConnectedProxyClient#redirectPacket
    }

    @com.github.derrop.proxy.api.network.PacketHandler(packetIds = {ProtocolIds.ClientBound.Login.SET_COMPRESSION}, protocolState = ProtocolState.LOGIN)
    private void handle(NetworkChannel channel, PacketLoginSetCompression setCompression) {
        channel.setCompression(setCompression.getThreshold());
    }

    @Override
    public String toString() {
        return "ProxyClient -> " + this.proxyClient.getAddress();
    }
}
