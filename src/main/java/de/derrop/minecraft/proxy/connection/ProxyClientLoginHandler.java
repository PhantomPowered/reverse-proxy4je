package de.derrop.minecraft.proxy.connection;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.minecraft.CryptManager;
import io.netty.util.concurrent.GenericFutureListener;
import net.md_5.bungee.EncryptionUtil;
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

    public ProxyClientLoginHandler(ConnectedProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        MCProxy.getInstance().removeProxyClient(this.proxyClient);
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
        System.err.println("Got kicked from " + this.proxyClient.getAddress() + " (Account: " + this.proxyClient.getAccountName() + "): " + kick.getMessage());
        this.proxyClient.connectionFailed();
    }

    @Override
    public void handle(EncryptionRequest request) throws Exception {
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
        this.proxyClient.connectionSuccess();

        this.proxyClient.getChannelWrapper().getHandle().pipeline().get(HandlerBoss.class).setHandler(new DownstreamBridge(this.proxyClient));
        throw CancelSendSignal.INSTANCE; // without this, the LoginSuccess would be recorded by ConnectedProxyClient#redirectPacket
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
    }

    @Override
    public void handle(Login login) throws Exception {
        this.proxyClient.setEntityId(login.getEntityId());
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
