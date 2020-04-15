package com.github.derrop.proxy.connection.login;

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.event.priority.EventPriority;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.minecraft.CryptManager;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.cipher.PacketCipherDecoder;
import com.github.derrop.proxy.network.cipher.PacketCipherEncoder;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.login.PacketLoginEncryptionRequest;
import com.github.derrop.proxy.protocol.login.PacketLoginEncryptionResponse;
import com.github.derrop.proxy.protocol.login.PacketLoginSetCompression;
import com.github.derrop.proxy.protocol.login.PacketPlayServerLoginSuccess;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.md_5.bungee.chat.ComponentSerializer;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PublicKey;

public class ProxyClientLoginHandler {

    @PacketHandler(packetIds = {ProtocolIds.ClientBound.Play.KICK_DISCONNECT}, protocolState = ProtocolState.PLAY)
    private void handle(ConnectedProxyClient proxyClient, PacketPlayServerKickPlayer kick) throws Exception {
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        proxyClient.setLastKickReason(reason);
        proxyClient.connectionFailed();
    }

    @PacketHandler(packetIds = {ProtocolIds.ServerBound.Login.ENCRYPTION_BEGIN}, protocolState = ProtocolState.LOGIN)
    // TODO can't we just use the packetId (if not defined) out of the Packet in the parameters?
    private void handle(ConnectedProxyClient proxyClient, PacketLoginEncryptionRequest request) throws Exception {
        if (proxyClient.getCredentials().isOffline()) {
            proxyClient.close();
            throw new IllegalStateException("Joined with an offline account on an online mode server");
        }

        final SecretKey secretKey = CryptManager.createNewSharedKey();
        String s = request.getServerId();
        PublicKey publicKey = CryptManager.decodePublicKey(request.getPublicKey());
        String s1 = (new BigInteger(CryptManager.getServerIdHash(s, publicKey, secretKey))).toString(16);

        try {
            proxyClient.getSessionService().joinServer(proxyClient.getAuthentication().getSelectedProfile(), proxyClient.getAuthentication().getAuthenticatedToken(), s1);
        } catch (AuthenticationException exception) {
            throw new Error("Failed to join server on auth servers!", exception);
        }

        byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretKey.getEncoded());
        byte[] verifyTokenEncrypted = CryptManager.encryptData(publicKey, request.getVerifyToken());
        proxyClient.write(new PacketLoginEncryptionResponse(secretKeyEncrypted, verifyTokenEncrypted));

        proxyClient.getWrappedChannel().pipeline().addBefore(
                NetworkUtils.LENGTH_DECODER,
                NetworkUtils.DECRYPT,
                new PacketCipherDecoder(secretKey)
        );
        proxyClient.getWrappedChannel().pipeline().addBefore(
                NetworkUtils.LENGTH_ENCODER,
                NetworkUtils.ENCRYPT,
                new PacketCipherEncoder(secretKey)
        );
    }
    @PacketHandler(packetIds = {ProtocolIds.ClientBound.Login.SUCCESS}, protocolState = ProtocolState.LOGIN, priority = EventPriority.FIRST)
    private void handle(NetworkChannel channel, PacketPlayServerLoginSuccess loginSuccess) throws Exception {
        channel.setProtocolState(ProtocolState.PLAY);
        //channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setHandler(new DownstreamBridge(this.connection));
        throw CancelProceedException.INSTANCE; // without this, the LoginSuccess would be recorded by ConnectedProxyClient#redirectPacket
    }

    @PacketHandler(packetIds = {ProtocolIds.ClientBound.Login.SET_COMPRESSION}, protocolState = ProtocolState.LOGIN)
    private void handle(NetworkChannel channel, PacketLoginSetCompression setCompression) {
        channel.setCompression(setCompression.getThreshold());
    }
}
