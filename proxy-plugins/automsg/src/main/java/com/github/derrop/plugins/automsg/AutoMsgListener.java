package com.github.derrop.plugins.automsg;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.player.id.PlayerId;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoMsgListener {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
    private static final Pattern MSG_PATTERN = Pattern.compile("\\[Freunde] (\\S+) » (\\S+): (.*)");
    private static final Pattern PARTY_RECEIVE_PATTERN = Pattern.compile("\\[Party] (\\S+) hat dich in eine Party eingeladen");
    private final AutoMsgDatabase database;

    public AutoMsgListener(AutoMsgDatabase database) {
        this.database = database;
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || event.getType() == ChatMessageType.ACTION_BAR) {
            return;
        }

        ServiceConnection connection = (ServiceConnection) event.getConnection();
        if (connection.getPlayer() != null) {
            return;
        }

        String message = ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(event.getMessage()));

        this.testPartyReceive(connection, message);
        this.testMsg(connection, message);
    }

    private void testPartyReceive(ServiceConnection connection, String message) {
        Matcher matcher = PARTY_RECEIVE_PATTERN.matcher(message);
        if (matcher.find()) {
            String inviter = matcher.group(1);
            this.joinParty(connection, inviter);
        }
    }

    private void joinParty(ServiceConnection connection, String inviter) {
        try {
            connection.chat("/party accept " + inviter);
            Thread.sleep(50);
            connection.chat("/p " + this.formatMessage(connection));
            Thread.sleep(50);
            connection.chat("/party leave");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }


    private void testMsg(ServiceConnection connection, String message) {
        Matcher matcher = MSG_PATTERN.matcher(message);
        if (matcher.find()) {
            String sender = matcher.group(1);
            if (!sender.equals(connection.getName())) {
                this.sendMsg(connection, sender);
            }
        }
    }

    private void sendMsg(ServiceConnection connection, String target) {
        connection.chat("/msg " + target + " " + this.formatMessage(connection));
    }

    private String formatMessage(ServiceConnection connection) {
        PlayerId id = connection.getLastConnectedPlayer();
        String message = id == null ? this.database.getDefaultMessage() : this.database.getMessage(id.getUniqueId());

        String date = FORMAT.format(connection.getLastDisconnectionTimestamp());
        long afkSeconds = (System.currentTimeMillis() - connection.getLastDisconnectionTimestamp()) / 1000;
        double afkMinutes = (double) afkSeconds / 60;

        String afkMinutesString = String.format("%.2f", afkMinutes);

        return message.replace("%date%", date).replace("%min%", afkMinutesString);
    }

}
