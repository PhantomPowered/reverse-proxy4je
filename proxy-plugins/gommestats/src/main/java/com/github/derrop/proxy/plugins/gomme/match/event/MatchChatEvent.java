package com.github.derrop.proxy.plugins.gomme.match.event;

import java.util.UUID;

public class MatchChatEvent extends MatchEvent {

    private final UUID senderId;
    private final String content;

    public MatchChatEvent(Type type, UUID senderId, String content) {
        super(type);
        this.senderId = senderId;
        this.content = content;
    }

    public UUID getSenderId() {
        return this.senderId;
    }

    public String getContent() {
        return this.content;
    }
}
