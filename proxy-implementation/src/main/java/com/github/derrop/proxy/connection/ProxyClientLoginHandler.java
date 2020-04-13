package com.github.derrop.proxy.connection;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.github.derrop.proxy.minecraft.CryptManager;
import net.md_5.bungee.EncryptionUtil;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.*;

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
    public void handle(Kick kick) throws Exception {
        if (this.proxyClient == null) {
            return;
        }
        BaseComponent[] reason = ComponentSerializer.parse(kick.getMessage());
        this.proxyClient.setLastKickReason(reason);
        this.proxyClient.connectionFailed();
    }

    @Override
    public void handle(EncryptionRequest request) throws Exception {
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
        this.proxyClient.getChannelWrapper().write(new EncryptionResponse(secretKeyEncrypted, verifyTokenEncrypted));

        BungeeCipher decrypt = EncryptionUtil.getCipher(false, secretKey);
        BungeeCipher encrypt = EncryptionUtil.getCipher(true, secretKey);
        this.proxyClient.getChannelWrapper().getHandle().pipeline().addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder(decrypt));
        this.proxyClient.getChannelWrapper().getHandle().pipeline().addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder(encrypt));
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception {
        this.proxyClient.getChannelWrapper().setProtocol(Protocol.GAME);

        this.proxyClient.getChannelWrapper().getHandle().pipeline().get(HandlerBoss.class).setHandler(new DownstreamBridge(this.connection));
        throw CancelSendSignal.INSTANCE; // without this, the LoginSuccess would be recorded by ConnectedProxyClient#redirectPacket
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
    }

    @Override
    public String toString() {
        return "ProxyClient -> " + this.proxyClient.getAddress();
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
        this.proxyClient.getChannelWrapper().setCompressionThreshold(setCompression.getThreshold());
    }
}
