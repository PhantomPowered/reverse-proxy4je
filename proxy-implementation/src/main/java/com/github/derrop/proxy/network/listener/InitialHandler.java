/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.network.listener;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.events.connection.player.PlayerLoginEvent;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.api.ping.ServerPing;
import com.github.derrop.proxy.api.repository.PlayerRepository;
import com.github.derrop.proxy.api.util.Callback;
import com.github.derrop.proxy.entity.player.DefaultPlayer;
import com.github.derrop.proxy.network.NetworkUtils;
import com.github.derrop.proxy.network.cipher.PacketCipherDecoder;
import com.github.derrop.proxy.network.cipher.PacketCipherEncoder;
import com.github.derrop.proxy.network.handler.HandlerEndpoint;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.handshake.PacketHandshakingClientSetProtocol;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInLoginRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutServerKickPlayer;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerKickPlayer;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutPong;
import com.github.derrop.proxy.protocol.status.client.PacketStatusOutResponse;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInPing;
import com.github.derrop.proxy.protocol.status.server.PacketStatusInRequest;
import com.github.derrop.proxy.util.HttpHelper;
import com.google.common.base.Preconditions;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.Util;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.ClientPacketListener;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.UUID;

public class InitialHandler {

    static final String INIT_STATE = "initialState";

    private final MCProxy proxy;

    public InitialHandler(MCProxy proxy) {
        this.proxy = proxy;
    }

    enum State {
        HANDSHAKE, STATUS, PING, USERNAME, ENCRYPT, FINISHED
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Status.START, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.STATUS)
    public void handle(NetworkChannel channel, PacketStatusInRequest statusRequest) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.STATUS, "Not expecting STATUS");

        final String motd = "\n§7Available/Online Accounts: §e" + MCProxy.getInstance().getFreeClients().size() + "§7/§e" + MCProxy.getInstance().getOnlineClients().size();

        channel.write(new PacketStatusOutResponse(Util.GSON.toJson(new ServerPing(
                new ServerPing.Protocol("§cProxy by §bderrop §cand §bderklaro", -1),
                new ServerPing.Players(0, 0, null),
                new TextComponent(motd),
                null
        ))));

        channel.setProperty(INIT_STATE, State.PING);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Status.PING, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.STATUS)
    public void handle(NetworkChannel channel, PacketStatusInPing ping) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.PING, "Not expecting PING");
        channel.write(new PacketStatusOutPong(ping.getTime()));
        disconnect(channel, "");
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Handshaking.SET_PROTOCOL, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.HANDSHAKING)
    public void handle(NetworkChannel channel, PacketHandshakingClientSetProtocol packetHandshakingClientSetProtocol) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.HANDSHAKE, "Not expecting HANDSHAKE");
        channel.setProperty("sentProtocol", packetHandshakingClientSetProtocol.getProtocolVersion());

        // Starting with FML 1.8, a "\0FML\0" token is appended to the handshake. This interferes
        // with Bungee's IP forwarding, so we detect it, and remove it from the host string, for now.
        // We know FML appends \00FML\00. However, we need to also consider that other systems might
        // add their own data to the end of the string. So, we just take everything from the \0 character
        // and save it for later.
        if (packetHandshakingClientSetProtocol.getHost().contains("\0")) {
            String[] split = packetHandshakingClientSetProtocol.getHost().split("\0", 2);
            packetHandshakingClientSetProtocol.setHost(split[0]);
            channel.setProperty("extraDataInHandshake", "\0" + split[1]);
        }

        // SRV records can end with a . depending on DNS / client.
        if (packetHandshakingClientSetProtocol.getHost().endsWith(".")) {
            packetHandshakingClientSetProtocol.setHost(packetHandshakingClientSetProtocol.getHost().substring(0, packetHandshakingClientSetProtocol.getHost().length() - 1));
        }

        channel.setProperty("virtualHost", InetSocketAddress.createUnresolved(packetHandshakingClientSetProtocol.getHost(), packetHandshakingClientSetProtocol.getPort()));

        switch (packetHandshakingClientSetProtocol.getRequestedProtocol()) {
            case 1:
                // Ping
                channel.setProperty(INIT_STATE, State.STATUS);
                channel.setProtocolState(ProtocolState.STATUS);
//                System.out.println("Ping: " + this);

                break;
            case 2:
                // Login
                channel.setProperty(INIT_STATE, State.USERNAME);
                channel.setProtocolState(ProtocolState.LOGIN);
                //System.out.println("Connect: " + this);

                if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(packetHandshakingClientSetProtocol.getProtocolVersion())) {
                    disconnect(channel, "We only support 1.8");
                    return;
                }
                break;
            default:
                throw new IllegalArgumentException("Cannot request protocol " + packetHandshakingClientSetProtocol.getRequestedProtocol());
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Login.START, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.LOGIN)
    public void handle(NetworkChannel channel, PacketLoginInLoginRequest loginRequest) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.USERNAME, "Not expecting USERNAME");
        channel.setProperty("requestedName", loginRequest.getData());

        if (loginRequest.getData().contains(".")) {
            disconnect(channel, "invalid name");
            return;
        }

        if (loginRequest.getData().length() > 16) {
            disconnect(channel, "name too long");
            return;
        }

        PacketLoginInEncryptionRequest request = EncryptionUtil.encryptRequest();
        channel.setProperty("encryptionRequest", request);
        channel.write(request);
        channel.setProperty(INIT_STATE, State.ENCRYPT);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Login.ENCRYPTION_RESPONSE, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.LOGIN)
    public void handle(NetworkChannel channel, final PacketLoginOutEncryptionResponse encryptResponse) throws Exception {
        Preconditions.checkState(channel.getProperty(INIT_STATE) == State.ENCRYPT, "Not expecting ENCRYPT");

        PacketLoginInEncryptionRequest encryptionRequest = channel.getProperty("encryptionRequest");
        SecretKey sharedKey = EncryptionUtil.getSecret(encryptResponse, encryptionRequest);

        channel.getWrappedChannel().pipeline().addBefore(NetworkUtils.LENGTH_DECODER, NetworkUtils.DECRYPT, new PacketCipherDecoder(sharedKey));
        channel.getWrappedChannel().pipeline().addBefore(NetworkUtils.LENGTH_ENCODER, NetworkUtils.ENCRYPT, new PacketCipherEncoder(sharedKey));

        String encName = URLEncoder.encode(channel.getProperty("requestedName"), "UTF-8");

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        for (byte[] bit : new byte[][]
                {
                        encryptionRequest.getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded()
                }) {
            sha.update(bit);
        }
        String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");

        //String preventProxy = (getSocketAddress() instanceof InetSocketAddress) ? "&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8") : "";
        String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash;// + preventProxy;

        Callback<String> handler = (result, error) -> {
            if (error == null) {
                LoginResult obj = Util.GSON.fromJson(result, LoginResult.class);
                if (obj != null && obj.getId() != null) {
                    UUID uniqueId = Util.getUUID(obj.getId());
                    /*if (uniqueId == null) {
                        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
                    }*/
                    finish(channel, uniqueId, obj);
                    return;
                }
                disconnect(channel, "offline mode not supported");
            } else {
                disconnect(channel, "failed to authenticate with mojang");
                error.printStackTrace();
            }
        };

        HttpHelper.getHTTPAsync(authURL, handler);
    }

    private void finish(NetworkChannel channel, UUID uniqueId, LoginResult result) {
        if (this.proxy.getServiceRegistry().getProviderUnchecked(PlayerRepository.class).getOnlinePlayer(uniqueId) != null) {
            disconnect(channel, "Already connected");
            return;
        }

        channel.getWrappedChannel().eventLoop().execute(() -> {
            if (!channel.isClosing()) {
                DefaultPlayer player = new DefaultPlayer(this.proxy, uniqueId, result, channel, channel.getProperty("sentProtocol"), 256);
                channel.write(new PacketLoginOutLoginSuccess(uniqueId.toString(), result.getName())); // With dashes in between
                channel.setProtocolState(ProtocolState.PLAY);
                channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setNetworkChannel(player);
                channel.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setChannelListener(new ClientPacketListener(player));

                ServiceConnection client = MCProxy.getInstance().findBestConnection(player);

                PlayerLoginEvent event = this.proxy.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerLoginEvent(player, client));
                if (!channel.isConnected()) {
                    return;
                }

                if (event.isCancelled()) {
                    disconnect(channel, event.getCancelReason() == null ? TextComponent.fromLegacyText("§cNo reason given") : event.getCancelReason());
                    return;
                }

                client = event.getTargetConnection();
                if (client == null) {
                    disconnect(channel, TextComponent.fromLegacyText("§7No client found"));
                    return;
                }

                player.useClient(client);
                channel.setProperty(INIT_STATE, State.FINISHED);
            }
        });
    }

    static boolean canSendKickMessage(NetworkChannel channel) {
        State state = channel.getProperty(INIT_STATE);
        return state == State.USERNAME || state == State.ENCRYPT || state == State.FINISHED;
    }

    static void disconnect(NetworkChannel channel, @NotNull String reason) {
        if (canSendKickMessage(channel)) {
            disconnect(channel, TextComponent.fromLegacyText(Constants.MESSAGE_PREFIX + reason));
        } else {
            channel.close();
        }
    }

    static void disconnect(NetworkChannel channel, final BaseComponent... reason) {
        if (canSendKickMessage(channel)) {
            if (channel.getProtocolState() == ProtocolState.PLAY) {
                channel.delayedClose(new PacketPlayServerKickPlayer(ComponentSerializer.toString(reason)));
            } else {
                channel.delayedClose(new PacketLoginOutServerKickPlayer(ComponentSerializer.toString(reason)));
            }
        } else {
            channel.close();
        }
    }

}
