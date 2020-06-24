package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol;

import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPing;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.handshake.LabyPacketHelloPong;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.login.*;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol.packets.play.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class LabyProtocol {

    private static final LabyProtocol INSTANCE = new LabyProtocol();
    private final Map<Class<? extends LabyPacket>, EnumConnectionState> protocol;
    private final Map<Integer, Class<? extends LabyPacket>> packets;

    public LabyProtocol() {
        this.protocol = new HashMap<>();
        this.packets = new HashMap<>();

        register(0, LabyPacketHelloPing.class, EnumConnectionState.HELLO);
        register(1, LabyPacketHelloPong.class, EnumConnectionState.HELLO);

        register(2, LabyPacketLoginStart.class, EnumConnectionState.LOGIN);
        register(3, LabyPacketLoginData.class, EnumConnectionState.LOGIN);
        register(4, LabyPacketLoginFriend.class, EnumConnectionState.LOGIN);
        register(5, LabyPacketLoginRequest.class, EnumConnectionState.LOGIN);
        register(6, LabyPacketLoginOptions.class, EnumConnectionState.LOGIN);
        register(7, LabyPacketLoginComplete.class, EnumConnectionState.LOGIN);
        register(8, LabyPacketLoginTime.class, EnumConnectionState.LOGIN);
        register(9, LabyPacketLoginVersion.class, EnumConnectionState.LOGIN);
        register(10, LabyPacketEncryptionRequest.class, EnumConnectionState.LOGIN);
        register(11, LabyPacketEncryptionResponse.class, EnumConnectionState.LOGIN);

        register(14, LabyPacketPlayPlayerOnline.class, EnumConnectionState.PLAY);
        register(16, LabyPacketPlayRequestAddFriend.class, EnumConnectionState.PLAY);
        register(17, LabyPacketPlayRequestAddFriendResponse.class, EnumConnectionState.PLAY);
        register(18, LabyPacketPlayRequestRemove.class, EnumConnectionState.PLAY);
        register(19, LabyPacketPlayDenyFriendRequest.class, EnumConnectionState.PLAY);
        register(20, LabyPacketPlayFriendRemove.class, EnumConnectionState.PLAY);
        register(21, LabyPacketPlayChangeOptions.class, EnumConnectionState.PLAY);
        register(22, LabyPacketPlayServerStatus.class, EnumConnectionState.PLAY);
        register(23, LabyPacketPlayFriendStatus.class, EnumConnectionState.PLAY);
        register(24, LabyPacketPlayFriendPlayingOn.class, EnumConnectionState.PLAY);
        register(25, LabyPacketPlayTyping.class, EnumConnectionState.PLAY);
        register(26, LabyPacketMojangStatus.class, EnumConnectionState.PLAY);

        register(27, LabyPacketActionPlay.class, EnumConnectionState.PLAY);
        register(28, LabyPacketActionPlayResponse.class, EnumConnectionState.PLAY);
        register(29, LabyPacketActionRequest.class, EnumConnectionState.PLAY);
        register(30, LabyPacketActionRequestResponse.class, EnumConnectionState.PLAY);
        register(31, LabyPacketUpdateCosmetics.class, EnumConnectionState.PLAY);
        register(32, LabyPacketAddonMessage.class, EnumConnectionState.PLAY);
        register(33, LabyPacketUserBadge.class, EnumConnectionState.PLAY);
        register(34, LabyPacketAddonDevelopment.class, EnumConnectionState.PLAY);

        register(60, LabyPacketDisconnect.class, EnumConnectionState.ALL);
        register(61, LabyPacketKick.class, EnumConnectionState.ALL);
        register(62, LabyPacketPing.class, EnumConnectionState.ALL);
        register(63, LabyPacketPong.class, EnumConnectionState.ALL);
        register(64, LabyPacketServerMessage.class, EnumConnectionState.ALL);
        register(65, LabyPacketMessage.class, EnumConnectionState.ALL);
        register(66, LabyPacketBanned.class, EnumConnectionState.ALL);
        register(67, LabyPacketChatVisibilityChange.class, EnumConnectionState.ALL);
        register(68, LabyPacketPlayServerStatusUpdate.class, EnumConnectionState.PLAY);
    }

    public static LabyProtocol getProtocol() {
        return INSTANCE;
    }

    public Map<Integer, Class<? extends LabyPacket>> getPackets() {
        return this.packets;
    }

    private void register(int id, Class<? extends LabyPacket> clazz, EnumConnectionState state) {
        try {
            this.packets.put(id, clazz);
            this.protocol.put(clazz, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LabyPacket getPacket(int id) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (!this.packets.containsKey(id)) {
            throw new RuntimeException("Packet with id " + id + " is not registered.");
        }
        return this.packets.get(id).getDeclaredConstructor().newInstance();
    }

    public int getPacketId(LabyPacket labyPacket) {
        for (Map.Entry<Integer, Class<? extends LabyPacket>> entry : this.packets.entrySet()) {
            Class<? extends LabyPacket> clazz = entry.getValue();
            if (clazz.isInstance(labyPacket)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Packet " + labyPacket + " is not registered.");
    }
}


