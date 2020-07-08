package com.github.derrop.proxy.plugins.gomme.clan.parser;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.player.id.PlayerIdStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.task.DefaultTask;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.plugins.gomme.GommeConstants;
import com.github.derrop.proxy.plugins.gomme.GommeServerType;
import com.github.derrop.proxy.plugins.gomme.clan.ClanInfo;
import com.github.derrop.proxy.plugins.gomme.clan.ClanMember;
import com.github.derrop.proxy.plugins.gomme.clan.ClanMessageRegistry;
import com.github.derrop.proxy.plugins.gomme.messages.Language;
import com.google.common.base.Preconditions;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClanParser {

    private static final String PENDING_CLAN_REQUEST_TASK = "GommePendingClanRequestTask";
    private static final String PENDING_CLAN_REQUEST_ID = "GommePendingClanRequestId";
    private static final String PENDING_CLAN_REQUEST = "GommePendingClanRequest";

    private final ServiceRegistry registry;
    private final ClanMessageRegistry messageRegistry;

    public ClanParser(ServiceRegistry registry) {
        this.registry = registry;
        this.messageRegistry = new ClanMessageRegistry();
    }

    public boolean hasPendingRequest(@NotNull ServiceConnection connection, @NotNull ClanId id) {
        return id.equals(connection.getProperty(PENDING_CLAN_REQUEST_ID));
    }

    public boolean hasPendingRequest(@NotNull ServiceConnection connection) {
        return connection.getProperty(PENDING_CLAN_REQUEST_TASK) != null;
    }

    public Task<ClanInfo> requestClanInfo(@NotNull ServiceConnection connection, @NotNull ClanId id) {
        if (connection.getProperty(PENDING_CLAN_REQUEST_TASK) != null) {
            if (!id.equals(connection.getProperty(PENDING_CLAN_REQUEST_ID))) {
                throw new IllegalArgumentException("There is already a pending clan request on this connection");
            }

            return connection.getProperty(PENDING_CLAN_REQUEST_TASK);
        }

        Task<ClanInfo> task = new DefaultTask<>();

        connection.setProperty(PENDING_CLAN_REQUEST_ID, id);
        connection.setProperty(PENDING_CLAN_REQUEST_TASK, task);

        connection.chat(id.getRequestMessage());

        return task;
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || event.getType() != ChatMessageType.SYSTEM) {
            return;
        }

        ServiceConnection connection = (ServiceConnection) event.getConnection();

        if (!this.hasPendingRequest(connection)) {
            return;
        }

        Language language = connection.getProperty(GommeConstants.SELECTED_LANGUAGE);
        if (language == null) {
            return;
        }

        String input = LegacyComponentSerializer.legacy().serialize(event.getMessage());

        this.messageRegistry.getMessage(language, GommeServerType.LOBBY, input).ifPresent(message -> {
            event.cancel(true);

            Map<String, String> data = message.getVariablesMapper() == null ? null : message.getVariablesMapper().apply(input);
            PendingClanRequest request = connection.getProperty(PENDING_CLAN_REQUEST);

            switch (message.getType()) {
                case CLAN_INFO_BEGIN:
                    connection.setProperty(PENDING_CLAN_REQUEST, new PendingClanRequest());
                    break;

                case CLAN_INFO_NAME:
                    if (request != null && data != null) {
                        request.setName(data.get("name"));
                    }
                    break;

                case CLAN_INFO_TAG:
                    if (request != null && data != null) {
                        request.setShortcut(data.get("tag"));
                    }
                    break;

                case CLAN_INFO_GENERAL_MEMBER_COUNT:
                    if (request != null && data != null) {
                        request.setGeneralMemberCount(Integer.parseInt(data.get("count")));
                        request.setMaxMemberCount(Integer.parseInt(data.get("max")));
                    }
                    break;

                case CLAN_INFO_LEADER_COUNT:
                    if (request != null && data != null) {
                        request.setMemberCount(ClanMember.Type.LEADER, Integer.parseInt(data.get("count")));
                    }
                    break;
                case CLAN_INFO_MODERATOR_COUNT:
                    if (request != null && data != null) {
                        request.setMemberCount(ClanMember.Type.MODERATOR, Integer.parseInt(data.get("count")));
                    }
                    break;
                case CLAN_INFO_MEMBER_COUNT:
                    if (request != null && data != null) {
                        request.setMemberCount(ClanMember.Type.MEMBER, Integer.parseInt(data.get("count")));
                    }
                    break;

                case CLAN_INFO_USER:
                    if (request == null || data == null) {
                        break;
                    }

                    Preconditions.checkNotNull(request.getReadingMemberType(), "not reading any member type but received member");
                    ClanMember.Rank rank = ClanMember.Rank.parseRank(data.get("rankColor"));

                    request.addMember(new PendingClanMember(data.get("name"), request.getReadingMemberType(), rank));

                    if (request.hasEnoughMembers()) {
                        this.finish(connection, request);
                    }
                    break;

                case CLAN_INFO_MORE:
                    if (request == null || data == null) {
                        break;
                    }

                    int unknown = Integer.parseInt(data.get("count"));
                    request.setUnknownMemberCount(unknown);

                    this.finish(connection, request);

                    break;
            }
        });

    }

    private void finish(ServiceConnection connection, PendingClanRequest request) {
        connection.removeProperty(PENDING_CLAN_REQUEST);
        connection.removeProperty(PENDING_CLAN_REQUEST_ID);

        Task<ClanInfo> task = connection.getProperty(PENDING_CLAN_REQUEST_TASK);
        connection.removeProperty(PENDING_CLAN_REQUEST_TASK);

        Constants.EXECUTOR_SERVICE.execute(() -> {
            ClanInfo clanInfo = request.toClanInfo(this.registry.getProviderUnchecked(PlayerIdStorage.class));

            if (task != null) {
                task.complete(clanInfo);
            }
        });
    }

}
