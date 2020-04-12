package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import lombok.Getter;
import net.md_5.bungee.*;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.*;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InitialHandler extends PacketHandler implements PendingConnection {

    private ChannelWrapper ch;
    @Getter
    private Handshake handshake;
    @Getter
    private LoginRequest loginRequest;
    private EncryptionRequest request;
    @Getter
    private final List<PluginMessage> relayMessages = new ArrayList<>();
    private State thisState = State.HANDSHAKE;
    private final Unsafe unsafe = new Unsafe() {
        @Override
        public void sendPacket(DefinedPacket packet) {
            ch.write(packet);
        }
    };
    @Getter
    private boolean onlineMode = true;
    @Getter
    private InetSocketAddress virtualHost;
    private String name;
    @Getter
    private UUID uniqueId;
    @Getter
    private UUID offlineId;
    @Getter
    private LoginResult loginProfile;
    @Getter
    private boolean legacy;
    @Getter
    private String extraDataInHandshake = "";

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return !ch.isClosing();
    }

    private enum State {

        HANDSHAKE, STATUS, PING, USERNAME, ENCRYPT, FINISHED
    }

    private boolean canSendKickMessage() {
        return thisState == State.USERNAME || thisState == State.ENCRYPT || thisState == State.FINISHED;
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
    }

    @Override
    public void exception(Throwable t) throws Exception {
        if (canSendKickMessage()) {
            disconnect(ChatColor.RED + Util.exception(t));
        } else {
            ch.close();
        }
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (packet.packet == null) {
            throw new IllegalArgumentException("Unexpected packet received during login process! " + BufUtil.dump(packet.buf, 16));
        }
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (PluginMessage.SHOULD_RELAY.apply(pluginMessage)) {
            relayMessages.add(pluginMessage);
        }
    }

    @Override
    public void handle(LegacyHandshake legacyHandshake) throws Exception {
        this.legacy = true;
        ch.close("outdated client");
    }

    @Override
    public void handle(LegacyPing ping) throws Exception {
        this.legacy = true;
        final boolean v1_5 = ping.isV1_5();

        ServerPing legacy = new ServerPing(new ServerPing.Protocol("§cProxy by §bderrop", -1),
                new ServerPing.Players(0, 0, null),
                new TextComponent(TextComponent.fromLegacyText("§7Please use the MC Version §c47")), null);

        String kickMessage;

        if (v1_5) {
            kickMessage = ChatColor.DARK_BLUE
                    + "\00" + 127
                    + '\00' + legacy.getVersion().getName()
                    + '\00' + getFirstLine(legacy.getDescription())
                    + '\00' + legacy.getPlayers().getOnline()
                    + '\00' + legacy.getPlayers().getMax();
        } else {
            // Clients <= 1.3 don't support colored motds because the color char is used as delimiter
            kickMessage = ChatColor.stripColor(getFirstLine(legacy.getDescription()))
                    + '\u00a7' + legacy.getPlayers().getOnline()
                    + '\u00a7' + legacy.getPlayers().getMax();
        }

        ch.close(kickMessage);
    }

    private static String getFirstLine(String str) {
        int pos = str.indexOf('\n');
        return pos == -1 ? str : str.substring(0, pos);
    }

    private ServerPing getPingInfo(String motd, int protocol) {
        return new ServerPing(
                new ServerPing.Protocol("§cProxy by §bderrop", -1),
                new ServerPing.Players(0, 0, null),
                motd, (Favicon) null
        );
    }

    @Override
    public void handle(StatusRequest statusRequest) throws Exception {
        Preconditions.checkState(thisState == State.STATUS, "Not expecting STATUS");

        final String motd = "§7To join: Contact §6Schul_Futzi#4633 §7on §9Discord\n§7Available/Online Accounts: §e" + MCProxy.getInstance().getFreeClients().size() + "§7/§e" + MCProxy.getInstance().getOnlineClients().size();
        final int protocol = (ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) ? handshake.getProtocolVersion() : 578;

        unsafe.sendPacket(new StatusResponse(Util.GSON.toJson(getPingInfo(motd, protocol))));

        thisState = State.PING;
    }

    @Override
    public void handle(PingPacket ping) throws Exception {
        Preconditions.checkState(thisState == State.PING, "Not expecting PING");
        unsafe.sendPacket(ping);
        disconnect("");
    }

    @Override
    public void handle(Handshake handshake) throws Exception {
        Preconditions.checkState(thisState == State.HANDSHAKE, "Not expecting HANDSHAKE");
        this.handshake = handshake;
        ch.setVersion(handshake.getProtocolVersion());

        // Starting with FML 1.8, a "\0FML\0" token is appended to the handshake. This interferes
        // with Bungee's IP forwarding, so we detect it, and remove it from the host string, for now.
        // We know FML appends \00FML\00. However, we need to also consider that other systems might
        // add their own data to the end of the string. So, we just take everything from the \0 character
        // and save it for later.
        if (handshake.getHost().contains("\0")) {
            String[] split = handshake.getHost().split("\0", 2);
            handshake.setHost(split[0]);
            extraDataInHandshake = "\0" + split[1];
        }

        // SRV records can end with a . depending on DNS / client.
        if (handshake.getHost().endsWith(".")) {
            handshake.setHost(handshake.getHost().substring(0, handshake.getHost().length() - 1));
        }

        this.virtualHost = InetSocketAddress.createUnresolved(handshake.getHost(), handshake.getPort());

        switch (handshake.getRequestedProtocol()) {
            case 1:
                // Ping
                thisState = State.STATUS;
                ch.setProtocol(Protocol.STATUS);
//                System.out.println("Ping: " + this);

                break;
            case 2:
                // Login
                thisState = State.USERNAME;
                ch.setProtocol(Protocol.LOGIN);
                //System.out.println("Connect: " + this);

                if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) {
                    disconnect("We only support 1.8");
                    return;
                }
                break;
            default:
                throw new IllegalArgumentException("Cannot request protocol " + handshake.getRequestedProtocol());
        }
    }

    @Override
    public void handle(LoginRequest loginRequest) throws Exception {
        Preconditions.checkState(thisState == State.USERNAME, "Not expecting USERNAME");
        this.loginRequest = loginRequest;

        if (getName().contains(".")) {
            disconnect("invalid name");
            return;
        }

        if (getName().length() > 16) {
            disconnect("name too long");
            return;
        }

        unsafe().sendPacket(request = EncryptionUtil.encryptRequest());
        thisState = State.ENCRYPT;
    }

    @Override
    public void handle(final EncryptionResponse encryptResponse) throws Exception {
        Preconditions.checkState(thisState == State.ENCRYPT, "Not expecting ENCRYPT");

        SecretKey sharedKey = EncryptionUtil.getSecret(encryptResponse, request);
        BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);
        ch.addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder(decrypt));
        BungeeCipher encrypt = EncryptionUtil.getCipher(true, sharedKey);
        ch.addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder(encrypt));

        String encName = URLEncoder.encode(InitialHandler.this.getName(), "UTF-8");

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        for (byte[] bit : new byte[][]
                {
                        request.getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded()
                }) {
            sha.update(bit);
        }
        String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");

        String preventProxy = (getSocketAddress() instanceof InetSocketAddress) ? "&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8") : "";
        String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash + preventProxy;

        Callback<String> handler = (result, error) -> {
            if (error == null) {
                LoginResult obj = Util.GSON.fromJson(result, LoginResult.class);
                if (obj != null && obj.getId() != null) {
                    loginProfile = obj;
                    name = obj.getName();
                    uniqueId = Util.getUUID(obj.getId());
                    finish();
                    return;
                }
                disconnect("offline mode not supported");
            } else {
                disconnect("failed to authenticate with mojang");
            }
        };

        HttpClient.get(authURL, ch.getHandle().eventLoop(), handler);
    }

    private void finish() {
        offlineId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + getName()).getBytes(Charsets.UTF_8));
        if (uniqueId == null) {
            uniqueId = offlineId;
        }

        if (MCProxy.getInstance().getOnlinePlayer(uniqueId) != null) {
            this.disconnect("Already connected");
            return;
        }

        ch.getHandle().eventLoop().execute(() -> {
            if (!ch.isClosing()) {
                UserConnection userCon = new UserConnection(ch, getName(), InitialHandler.this);
                userCon.setCompressionThreshold(256);
                userCon.init();

                unsafe.sendPacket(new LoginSuccess(getUniqueId().toString(), getName())); // With dashes in between
                ch.setProtocol(Protocol.GAME);

                ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(new UpstreamBridge(userCon));

                ConnectedProxyClient client = MCProxy.getInstance().findBestProxyClient(uniqueId);
                if (client == null) {
                    disconnect("No client available");
                    return;
                }

                userCon.useClient(client);

                thisState = State.FINISHED;
            }
        });
    }

    @Override
    public void handle(LoginPayloadResponse response) throws Exception {
        disconnect("Unexpected custom LoginPayloadResponse");
    }

    @Override
    public void disconnect(String reason) {
        if (canSendKickMessage()) {
            disconnect(TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + reason));
        } else {
            ch.close();
        }
    }

    @Override
    public void disconnect(final BaseComponent... reason) {
        if (canSendKickMessage()) {
            ch.delayedClose(new Kick(ComponentSerializer.toString(reason)));
        } else {
            ch.close();
        }
    }

    @Override
    public void disconnect(BaseComponent reason) {
        disconnect(new BaseComponent[]
                {
                        reason
                });
    }

    @Override
    public String getName() {
        return (name != null) ? name : (loginRequest == null) ? null : loginRequest.getData();
    }

    @Override
    public int getVersion() {
        return (handshake == null) ? -1 : handshake.getProtocolVersion();
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getSocketAddress();
    }

    @Override
    public SocketAddress getSocketAddress() {
        return ch.getRemoteAddress();
    }

    @Override
    public Unsafe unsafe() {
        return unsafe;
    }

    @Override
    public void setOnlineMode(boolean onlineMode) {
        Preconditions.checkState(thisState == State.USERNAME, "Can only set online mode status whilst state is username");
        this.onlineMode = onlineMode;
    }

    @Override
    public void setUniqueId(UUID uuid) {
        Preconditions.checkState(thisState == State.USERNAME, "Can only set uuid while state is username");
        Preconditions.checkState(!onlineMode, "Can only set uuid when online mode is false");
        this.uniqueId = uuid;
    }

    @Override
    public String getUUID() {
        return uniqueId.toString().replace("-", "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        String currentName = getName();
        if (currentName != null) {
            sb.append(currentName);
            sb.append(',');
        }

        sb.append(getSocketAddress());
        sb.append("] <-> InitialHandler");

        return sb.toString();
    }

    @Override
    public boolean isConnected() {
        return !ch.isClosed();
    }
}
