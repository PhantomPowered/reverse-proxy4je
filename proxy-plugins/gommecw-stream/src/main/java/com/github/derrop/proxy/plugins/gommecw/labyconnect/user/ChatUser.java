package com.github.derrop.proxy.plugins.gommecw.labyconnect.user;

import com.mojang.authlib.GameProfile;

public class ChatUser {

    private GameProfile gameProfile;
    private UserStatus status;
    private String statusMessage;
    private ServerInfo currentServerInfo;
    private int unreadMessages;
    private String timeZone;
    private long lastOnline;
    private long firstJoined;
    private int contactAmount;
    private boolean party;

    public ChatUser(GameProfile gameProfile, UserStatus status, String statusMessage, ServerInfo currentServerInfo, int unreadMessages, String timeZone, long lastOnline, long firstJoined, int contactAmount, boolean party) {
        this.unreadMessages = 0;
        this.gameProfile = gameProfile;
        this.status = status;
        this.statusMessage = statusMessage;
        this.currentServerInfo = currentServerInfo;
        this.unreadMessages = unreadMessages;
        this.timeZone = timeZone;
        this.lastOnline = lastOnline;
        this.firstJoined = firstJoined;
        this.contactAmount = contactAmount;
        this.party = party;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ServerInfo getCurrentServerInfo() {
        return this.currentServerInfo;
    }

    public void setCurrentServerInfo(ServerInfo currentServerInfo) {
        this.currentServerInfo = currentServerInfo;
    }

    public int getUnreadMessages() {
        return this.unreadMessages;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public long getFirstJoined() {
        return this.firstJoined;
    }

    public int getContactAmount() {
        return this.contactAmount;
    }

    public boolean isFriendRequest() {
        return this instanceof ChatRequest;
    }

    public boolean isOnline() {
        return (this.status != UserStatus.OFFLINE);
    }

    public boolean equals(ChatUser chatUser) {
        return this.party && chatUser.party || !chatUser.party && !this.party && chatUser.getGameProfile().getId().equals(this.gameProfile.getId());
    }
}


