package com.github.derrop.proxy.plugins.gommecw.labyconnect;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatRequest;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ChatUser;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.ServerInfo;
import com.github.derrop.proxy.plugins.gommecw.labyconnect.user.UserStatus;
import com.mojang.authlib.GameProfile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// we cannot use the methods out of our ProtoBuf because they utilize the VarInt, LabyMod uses the ints
public class LabyBufferUtils {

    public static int getVarIntSize(int input) {
        for (int var1 = 1; var1 < 5; var1++) {
            if ((input & -1 << var1 * 7) == 0)
                return var1;
        }
        return 5;
    }

    public static byte[] readArray(ProtoBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buf.readByte();
        }
        return bytes;
    }

    public static void writeArray(ProtoBuf buf, byte[] bytes) {
        buf.writeInt(bytes.length);
        for (byte b : bytes) {
            buf.writeByte(b);
        }
    }

    public static String readString(ProtoBuf buf) {
        return new String(readArray(buf), StandardCharsets.UTF_8);
    }

    public static void writeString(ProtoBuf buf, String s) {
        writeArray(buf, s.getBytes(StandardCharsets.UTF_8));
    }

    public static ChatUser readChatUser(ProtoBuf buf) {
        String username = readString(buf);
        UUID uuid = UUID.fromString(readString(buf));
        String statusMessage = readString(buf);
        UserStatus status = readUserStatus(buf);

        boolean request = buf.readBoolean();
        String timeZone = readString(buf);
        int contactsAmount = buf.readInt();
        long lastOnline = buf.readLong();
        long firstJoined = buf.readLong();

        ServerInfo serverInfo = readServerInfo(buf);

        if (request) {
            return new ChatRequest(new GameProfile(uuid, username));
        }
        return new ChatUser(new GameProfile(uuid, username), status, statusMessage, serverInfo, 0, timeZone, lastOnline, firstJoined, contactsAmount, false);
    }

    public static void writeChatUser(ProtoBuf buf, ChatUser user) {
        writeString(buf, user.getGameProfile().getName());
        writeString(buf, user.getGameProfile().getId().toString());
        writeString(buf, user.getStatusMessage());
        writeUserStatus(buf, user.getStatus());
        buf.writeBoolean(user.isFriendRequest());
        writeString(buf, user.getTimeZone());
        buf.writeInt(user.getContactAmount());
        buf.writeLong(user.getLastOnline());
        buf.writeLong(user.getFirstJoined());
        writeServerInfo(buf, user.getCurrentServerInfo());
    }

    public static ServerInfo readServerInfo(ProtoBuf buf) {
        String serverIp = readString(buf);
        int serverPort = buf.readInt();
        if (buf.readBoolean()) {
            return new ServerInfo(serverIp, serverPort, readString(buf));
        }
        return new ServerInfo(serverIp, serverPort);
    }

    public static void writeServerInfo(ProtoBuf buf, ServerInfo info) {
        if (info == null) {
            info = new ServerInfo("", 0);
        }
        writeString(buf, (info.getServerIp() == null) ? "" : info.getServerIp());
        buf.writeInt(info.getServerPort());
        if (info.getSpecifiedServerName() != null) {
            buf.writeBoolean(true);
            writeString(buf, info.getSpecifiedServerName());
        } else {
            buf.writeBoolean(false);
        }
    }

    public static void writeUserStatus(ProtoBuf buf, UserStatus status) {
        buf.writeByte(status.getId());
    }

    public static UserStatus readUserStatus(ProtoBuf buf) {
        return UserStatus.getById(buf.readByte());
    }

}
