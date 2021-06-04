package com.github.phantompowered.proxy.connection.handler;

import com.github.phantompowered.proxy.api.block.half.HorizontalHalf;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.chat.HistoricalMessage;
import com.github.phantompowered.proxy.api.connection.Connection;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.entity.EntityStatusType;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.event.Cancelable;
import com.github.phantompowered.proxy.api.event.Event;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.events.connection.ChatEvent;
import com.github.phantompowered.proxy.api.events.connection.PluginMessageEvent;
import com.github.phantompowered.proxy.api.events.connection.service.ServiceExperienceChangeEvent;
import com.github.phantompowered.proxy.api.events.connection.service.TitleReceiveEvent;
import com.github.phantompowered.proxy.api.events.connection.service.entity.EntityAnimationEvent;
import com.github.phantompowered.proxy.api.events.connection.service.entity.EntityMoveEvent;
import com.github.phantompowered.proxy.api.events.connection.service.entity.status.EntityStatusEvent;
import com.github.phantompowered.proxy.api.events.connection.service.entity.status.SelfEntityStatusEvent;
import com.github.phantompowered.proxy.api.events.connection.service.inventory.ServiceInventoryOpenEvent;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.network.ByteBufUtils;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.connection.AppendedActionBar;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.DefaultServiceInventory;
import com.github.phantompowered.proxy.connection.cache.handler.EntityCache;
import com.github.phantompowered.proxy.connection.cache.handler.PlayerInfoCache;
import com.github.phantompowered.proxy.connection.player.DefaultPlayer;
import com.github.phantompowered.proxy.network.wrapper.DecodedPacket;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.client.inventory.PacketPlayClientConfirmTransaction;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerRespawn;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityAnimation;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityMetadata;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityStatus;
import com.github.phantompowered.proxy.protocol.play.server.entity.PacketPlayServerEntityTeleport;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerConfirmTransaction;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerOpenWindow;
import com.github.phantompowered.proxy.protocol.play.server.message.*;
import com.github.phantompowered.proxy.protocol.play.server.player.PacketPlayServerSetExperience;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerSpawnPosition;
import com.github.phantompowered.proxy.protocol.play.shared.PacketPlayKeepAlive;
import com.github.phantompowered.proxy.text.ProxyLegacyHoverEventSerializer;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ServerPacketHandler {

    private static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.builder()
            .legacyHoverEventSerializer(ProxyLegacyHoverEventSerializer.INSTANCE)
            .build();

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.PLAYER_LIST_HEADER_FOOTER, directions = ProtocolDirection.TO_CLIENT)
    public void handleTabUpdate(ConnectedProxyClient client, PacketPlayServerPlayerListHeaderFooter packet) {
        Component header = GsonComponentSerializer.gson().deserialize(packet.getHeader());
        Component footer = GsonComponentSerializer.gson().deserialize(packet.getFooter());

        if (client.getConnection().setTabHeaderAndFooter(header, footer)) {
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.SET_EXPERIENCE, directions = ProtocolDirection.TO_CLIENT)
    public void handleSetExperience(ConnectedProxyClient client, PacketPlayServerSetExperience packet) {
        client.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new ServiceExperienceChangeEvent(client.getConnection(), packet.getCurrentXP(), packet.getMaxXP(), packet.getLevel()));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ANIMATION, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityAnimation(ConnectedProxyClient client, PacketPlayServerEntityAnimation packet) {
        Entity entity = client.getPacketCache().getHandler(EntityCache.class).getEntities().get(packet.getEntityId());
        if (entity == null) {
            return;
        }
        EntityAnimationEvent.AnimationType type = EntityAnimationEvent.AnimationType.values()[packet.getType()];

        EventManager eventManager = client.getServiceRegistry().getProviderUnchecked(EventManager.class);
        eventManager.callEvent(new EntityAnimationEvent(entity, type));
    }

    @PacketHandler(directions = ProtocolDirection.TO_CLIENT)
    public void handleGeneral(ConnectedProxyClient client, DecodedPacket packet) {
        client.redirectPacket(packet.getProtoBuf().clone(), packet.getPacket());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.PLAYER_INFO, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING)
    public void modifyPlayerInfo(ConnectedProxyClient client, PacketPlayServerPlayerInfo packet) {
        Player player = client.getRedirector();
        if (player == null) {
            return;
        }

        client.getPacketCache().getHandler(PlayerInfoCache.class).replaceOwn(player, packet);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.REDIRECTING)
    public void modifyActionBarRedirect(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        this.modifyActionBar(client, packet);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.PLAY)
    public void modifyActionBarPlay(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        this.modifyActionBar(client, packet);
    }

    private void modifyActionBar(ConnectedProxyClient client, PacketPlayServerChatMessage packet) {
        if (client.getRedirector() == null) {
            return;
        }

        DefaultPlayer player = (DefaultPlayer) client.getRedirector();
        if (packet.getPosition() != ChatMessageType.ACTION_BAR.ordinal()) {
            return;
        }

        Component component = GsonComponentSerializer.gson().deserialize(packet.getMessage());
        String original = LegacyComponentSerializer.legacySection().serialize(component);

        for (AppendedActionBar actionBar : player.getActionBars()) {
            String message = actionBar.getMessage().get();
            if (message == null) {
                player.getActionBars().remove(actionBar);
                continue;
            }

            HorizontalHalf side = actionBar.getSide();
            original = side == HorizontalHalf.LEFT ? message + original : original + message;
        }

        packet.setMessage(GsonComponentSerializer.gson().serialize(Component.text(original)));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ENTITY_STATUS, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityStatus(ConnectedProxyClient client, PacketPlayServerEntityStatus packet) {
        boolean self = packet.getEntityId() == client.getEntityId();
        Cancelable event;

        if (self) {
            EntityStatusType statusType = EntityStatusType.ofSelfPlayer(packet.getStatus());
            if (statusType == null) {
                System.err.println("Unknown EntityStatusType received: " + packet.getStatus() + " for entity SELF");
                return;
            }

            event = new SelfEntityStatusEvent(client.getConnection(), statusType);
        } else {
            Entity entity = client.getConnection().getWorldDataProvider().getEntityInWorld(packet.getEntityId());
            if (entity == null) {
                return;
            }

            EntityStatusType statusType = EntityStatusType.ofOtherEntity(entity.getClass(), packet.getStatus());
            if (statusType == null) {
                System.err.println(String.format("Unknown EntityStatusType received: %s for entity %d (type: %s)", packet.getStatus(), packet.getEntityId(), entity.getType() == null ? entity.getLivingType() : entity.getType()));
                // use println String.format because printf would somehow print the string in multiple lines
                return;
            }

            event = new EntityStatusEvent(client.getConnection(), entity, statusType);
        }

        client.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent((Event) event);

        if (event.isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.RESPAWN, directions = ProtocolDirection.TO_CLIENT)
    public void handleRespawn(ConnectedProxyClient client, PacketPlayServerRespawn packet) {
        client.getConnection().setSneaking(false);
        client.getConnection().setSprinting(false);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.SPAWN_POSITION, directions = ProtocolDirection.TO_CLIENT)
    public void handleSpawnPosition(ConnectedProxyClient client, PacketPlayServerSpawnPosition packet) {
        client.getConnection().updateLocation(packet.getSpawnLocation());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.OPEN_WINDOW, directions = ProtocolDirection.TO_CLIENT)
    public void handleWindowOpen(ConnectedProxyClient client, PacketPlayServerOpenWindow packet) {
        client.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new ServiceInventoryOpenEvent(client.getConnection(), packet.getWindowTitle(), packet.getInventoryType(), packet.getSlotCount()));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ENTITY_TELEPORT, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityTeleport(ConnectedProxyClient client, PacketPlayServerEntityTeleport teleport) {
        Location location = teleport.getLocation();

        if (teleport.getEntityId() != client.getEntityId()) {
            Entity entity = client.getConnection().getWorldDataProvider().getEntityInWorld(teleport.getEntityId());
            if (entity == null) {
                return;
            }

            entity.teleport(location);
            client.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new EntityMoveEvent(client.getConnection(), entity, entity.getLocation(), location));
            return;
        }

        client.getConnection().updateLocation(location);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.ENTITY_METADATA, directions = ProtocolDirection.TO_CLIENT)
    public void handleEntityMeta(ConnectedProxyClient client, PacketPlayServerEntityMetadata meta) {
        if (client.getEntityId() == meta.getEntityId()) {
            return;
        }

        Entity entity = client.getConnection().getWorldDataProvider().getEntityInWorld(client.getEntityId());
        if (entity == null) {
            return;
        }

        entity.getCallable().handleEntityPacket(meta);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.POSITION, directions = ProtocolDirection.TO_CLIENT)
    public void handlePosition(ConnectedProxyClient client, PacketPlayServerPosition position) {
        Location location = new Location(position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
        client.getConnection().updateLocation(location);
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.KEEP_ALIVE, directions = ProtocolDirection.TO_CLIENT)
    public void handleKeepAlive(ConnectedProxyClient client, PacketPlayKeepAlive alive) {
        client.write(alive);
        client.setLastAlivePacket(System.currentTimeMillis());
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.LOGIN, directions = ProtocolDirection.TO_CLIENT)
    public void handleLogin(ConnectedProxyClient client, PacketPlayServerLogin login) {
        client.setEntityId(login.getEntityId());
        client.connectionSuccess();
        client.setDimension(login.getDimension());

        ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeString("vanilla", buf);
        client.getConnection().sendCustomPayload("MC|Brand", ByteBufUtils.toArray(buf));
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CUSTOM_PAYLOAD, directions = ProtocolDirection.TO_CLIENT)
    public void handlePluginMessage(ConnectedProxyClient client, PacketPlayServerPluginMessage pluginMessage) {
        PluginMessageEvent event = new PluginMessageEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, pluginMessage.getTag(), pluginMessage.getData());
        if (client.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());

        Connection connection = client.getRedirector();
        if (connection == null) {
            return;
        }

        connection.sendPacket(pluginMessage);
        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.KICK_DISCONNECT, directions = ProtocolDirection.TO_CLIENT)
    public void handleKick(ConnectedProxyClient client, PacketPlayServerKickPlayer kick) throws Exception {
        Component reason = GsonComponentSerializer.gson().deserialize(kick.getMessage());

        client.handleDisconnect(reason);
        client.setLastKickReason(reason);

        // This disables auto reconnect for this client: client.getConnection().close();

        throw CancelProceedException.INSTANCE;
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.CHAT, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerChatMessage chat) {
        try {
            ChatEvent event = new ChatEvent(client.getConnection(), ProtocolDirection.TO_CLIENT, ChatMessageType.values()[chat.getPosition()], SERIALIZER.deserialize(chat.getMessage()));
            if (client.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
                throw CancelProceedException.INSTANCE;
            }

            chat.setMessage(SERIALIZER.serialize(event.getMessage()));

            if (event.getType() == ChatMessageType.CHAT || event.getType() == ChatMessageType.SYSTEM) {
                client.getConnection().getReceivedMessages().add(HistoricalMessage.now(event.getMessage()));
            }
        } catch (JsonParseException ignored) {
            // hack
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_CLIENT)
    public void handleTabComplete(ConnectedProxyClient client, PacketPlayServerTabCompleteResponse response) {
        if (client.getRedirector() == null) {
            return;
        }

        DefaultPlayer player = (DefaultPlayer) client.getRedirector();

        if (player.getLastCommandCompleteRequest() != null) {
            player.setLastCommandCompleteRequest(null);
            response.getCommands().add("/proxy");
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.RESPAWN, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerRespawn respawn) {
        client.setDimension(respawn.getDimension());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TITLE, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerTitle title) {
        ServiceConnection connection = client.getConnection();
        TitleReceiveEvent event;
        if (title.getAction() == PacketPlayServerTitle.Action.TITLE) {
            event = new TitleReceiveEvent(connection, title.getText(), TitleReceiveEvent.TitleUpdateType.TITLE);
        } else if (title.getAction() == PacketPlayServerTitle.Action.SUBTITLE) {
            event = new TitleReceiveEvent(connection, title.getText(), TitleReceiveEvent.TitleUpdateType.SUB_TITLE);
        } else if (title.getAction() == PacketPlayServerTitle.Action.TIMES) {
            event = new TitleReceiveEvent(connection, title.getFadeIn(), title.getStay(), title.getFadeOut());
        } else if (title.getAction() == PacketPlayServerTitle.Action.RESET || title.getAction() == PacketPlayServerTitle.Action.CLEAR) {
            event = new TitleReceiveEvent(connection);
        } else {
            return;
        }

        if (connection.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.OPEN_WINDOW, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerOpenWindow packet) {
        ((DefaultServiceInventory) client.getConnection().getInventory()).setOpenWindowId(packet.getWindowId());
    }

    @PacketHandler(packetIds = ProtocolIds.ToClient.Play.TRANSACTION, directions = ProtocolDirection.TO_CLIENT)
    public void handle(ConnectedProxyClient client, PacketPlayServerConfirmTransaction packet) {
        if (client.getConnection().getInventory().completeTransaction(packet.getActionNumber())) {
            throw CancelProceedException.INSTANCE;
        }

        if (!packet.isNoResponse() && client.getConnection().getPlayer() == null) {
            client.write(new PacketPlayClientConfirmTransaction(packet.getWindowId(), (short) packet.getId(), true));
        }
    }
}
