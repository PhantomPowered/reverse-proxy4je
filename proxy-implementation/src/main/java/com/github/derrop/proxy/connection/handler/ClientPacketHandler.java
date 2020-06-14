package com.github.derrop.proxy.connection.handler;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.command.CommandMap;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.exception.PermissionDeniedException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.player.GameMode;
import com.github.derrop.proxy.api.connection.player.inventory.ClickType;
import com.github.derrop.proxy.api.entity.Entity;
import com.github.derrop.proxy.api.event.Cancelable;
import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.EventPriority;
import com.github.derrop.proxy.api.events.connection.ChatEvent;
import com.github.derrop.proxy.api.events.connection.PluginMessageEvent;
import com.github.derrop.proxy.api.events.connection.player.*;
import com.github.derrop.proxy.api.events.connection.player.interact.PlayerAttackEntityEvent;
import com.github.derrop.proxy.api.events.connection.player.interact.PlayerInteractAtEntityEvent;
import com.github.derrop.proxy.api.events.connection.player.interact.PlayerInteractEntityEvent;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.PacketHandler;
import com.github.derrop.proxy.api.network.exception.CancelProceedException;
import com.github.derrop.proxy.connection.BasicServiceConnection;
import com.github.derrop.proxy.connection.player.DefaultPlayer;
import com.github.derrop.proxy.network.wrapper.DecodedPacket;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.client.*;
import com.github.derrop.proxy.protocol.play.client.entity.PacketPlayClientEntityAction;
import com.github.derrop.proxy.protocol.play.client.entity.PacketPlayClientUseEntity;
import com.github.derrop.proxy.protocol.play.client.inventory.PacketPlayClientClickWindow;
import com.github.derrop.proxy.protocol.play.client.inventory.PacketPlayClientCloseWindow;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientLook;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPosition;
import com.github.derrop.proxy.protocol.play.client.position.PacketPlayClientPositionLook;
import com.github.derrop.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.derrop.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.derrop.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.derrop.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ClientPacketHandler {

    @PacketHandler(protocolState = ProtocolState.PLAY, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.LAST)
    public void handleGeneral(DefaultPlayer player, DecodedPacket packet) {
        if (player.getConnectedClient() != null && player.getConnectedClient().isConnected()) {

            if (packet.getPacket() != null && player.getConnectedClient() instanceof BasicServiceConnection) {
                ((BasicServiceConnection) player.getConnectedClient()).getEntityRewrite().updatePacketToServer(packet.getPacket(), player.getEntityId(), player.getConnectedClient().getEntityId());

                ((BasicServiceConnection) player.getConnectedClient()).getClient().handleClientPacket(packet.getPacket());

                ((BasicServiceConnection) player.getConnectedClient()).getClient().getVelocityHandler().handlePacket(ProtocolDirection.TO_SERVER, packet.getPacket());
            }

            // rewrite to allow modifications by the packet handlers
            player.getConnectedClient().networkUnsafe().sendPacket(packet.getPacket() != null ? packet.getPacket() : packet);
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.ENTITY_ACTION, directions = ProtocolDirection.TO_SERVER)
    public void handleAction(DefaultPlayer player, PacketPlayClientEntityAction packet) {
        if (player.getConnectedClient() == null || !(player.getConnectedClient() instanceof BasicServiceConnection)) {
            return;
        }
        BasicServiceConnection connection = (BasicServiceConnection) player.getConnectedClient();

        switch (packet.getAction()) {
            case START_SNEAKING:
                connection.setSneaking(true);
                break;

            case STOP_SNEAKING:
                connection.setSneaking(false);
                break;

            case START_SPRINTING:
                connection.setSprinting(true);
                break;

            case STOP_SPRINTING:
                connection.setSprinting(false);
                break;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_DIG, directions = ProtocolDirection.TO_SERVER)
    public void handleLeftClick(DefaultPlayer player, PacketPlayClientPlayerDigging packet) {
        if (packet.getAction() != PacketPlayClientPlayerDigging.Action.START_DESTROY_BLOCK &&
                packet.getAction() != PacketPlayClientPlayerDigging.Action.ABORT_DESTROY_BLOCK &&
                packet.getAction() != PacketPlayClientPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            return;
        }
        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
        player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (event.isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        if (event.getAction() != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            if (player.getConnectedClient() != null) {
                //player.getConnectedClient().sendPacket(new PacketPlayClientUseEntity()); TODO what should we send when we rightclick into the air?
            }
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.USE_ENTITY, directions = ProtocolDirection.TO_SERVER)
    public void handleUseEntity(DefaultPlayer player, PacketPlayClientUseEntity packet) {
        if (player.getConnectedClient() == null) {
            return;
        }
        Entity entity = player.getConnectedClient().getWorldDataProvider().getEntityInWorld(packet.getEntityId());

        Event event;

        switch (packet.getAction()) {
            case ATTACK: {
                event = new PlayerAttackEntityEvent(player, entity);
            }
            break;

            case INTERACT: {
                event = new PlayerInteractEntityEvent(player, entity);
            }
            break;

            case INTERACT_AT: {
                event = new PlayerInteractAtEntityEvent(player, entity, packet.getHitVector());
            }
            break;

            default:
                throw new IllegalStateException("Received unknown action " + packet.getAction());
        }

        player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (((Cancelable) event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

    private boolean isTargettingBlock(ServiceConnection connection) {
        int distance = connection.getWorldDataProvider().getOwnGameMode() == GameMode.CREATIVE ? Constants.CREATIVE_PLACE_DISTANCE : Constants.SURVIVAL_PLACE_DISTANCE;
        try {
            Location targetedBlock = connection.getTargetBlock(distance);
            if (targetedBlock != null) {
                return true;
            }
        } catch (IllegalStateException exception) {
        }
        return false;
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.ARM_ANIMATION, directions = ProtocolDirection.TO_SERVER)
    public void handleArmAnimation(DefaultPlayer player, PacketPlayClientArmAnimation packet) {
        if (player.getConnectedClient() == null || this.isTargettingBlock(player.getConnectedClient())) {
            // Left click block is sent in the Digging packet
            return;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_AIR);
        player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (event.isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        if (event.getAction() != PlayerInteractEvent.Action.LEFT_CLICK_AIR) {
            //player.getConnectedClient().sendPacket(new PacketPlayClientUseEntity()); TODO what should we send when we rightclick into the air?
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_PLACE, directions = ProtocolDirection.TO_SERVER)
    public void handleBlockPlace(DefaultPlayer player, PacketPlayClientBlockPlace packet) {
        if (player.getConnectedClient() == null) {
            return;
        }
        if (packet.getPlacedBlockDirection() == 255) {
            boolean block = this.isTargettingBlock(player.getConnectedClient());
            PlayerInteractEvent event = new PlayerInteractEvent(player, block ? PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK : PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
            player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
            if (event.isCancelled()) {
                throw CancelProceedException.INSTANCE;
            }
            return;
        }

        PlayerBlockPlaceEvent event = player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerBlockPlaceEvent(player, packet.getLocation(), packet.getStack()));
        if (event.isCancelled()) {
            // player.sendPacket(new PacketPlayServerBlockChange(packet.getPos(), player.getConnectedClient().getBlockAccess().getBlockState(packet.getPos()))); TODO should we send this?
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.WINDOW_CLICK, directions = ProtocolDirection.TO_SERVER)
    public void handleWindowClick(DefaultPlayer player, PacketPlayClientClickWindow packet) {
        ClickType click = packet.getClick();
        if (click == null) {
            return;
        }

        PlayerInventoryClickEvent event = player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerInventoryClickEvent(player, packet.getSlot(), click));
        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerSetSlot((byte) -1, -1, ItemStack.AIR));
            player.sendPacket(new PacketPlayServerSetSlot(player.getInventory().getWindowId(), packet.getSlot(), player.getInventory().getItem(packet.getSlot())));
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CLOSE_WINDOW, directions = ProtocolDirection.TO_SERVER)
    public void handleWindowClose(DefaultPlayer player, PacketPlayClientCloseWindow packet) {
        PlayerInventoryCloseEvent event = player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerInventoryCloseEvent(player));
        if (event.isCancelled() && packet.getWindowId() == player.getInventory().getWindowId()) {
            player.getInventory().open();
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.POSITION, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientPosition position) {
        this.updatePosition(player, position);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.POSITION_LOOK, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientPositionLook position) {
        this.updatePosition(player, position);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.LOOK, directions = ProtocolDirection.TO_SERVER)
    public void handlePosition(DefaultPlayer player, PacketPlayClientLook position) {
        this.updatePosition(player, position);
    }

    private void updatePosition(DefaultPlayer player, PacketPlayClientPlayerPosition position) {
        ServiceConnection connection = player.getConnectedClient();
        if (connection == null) {
            return;
        }

        Location newLocation = position.getLocation(connection.getLocation());

        if (newLocation == null) {
            return;
        }

        PlayerMoveEvent event = player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerMoveEvent(player, connection.getLocation(), newLocation));
        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerPosition(event.getTo()));
            throw CancelProceedException.INSTANCE;
        }

        connection.updateLocation(newLocation);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_DIG, directions = ProtocolDirection.TO_SERVER)
    public void handleBlockDig(DefaultPlayer player, PacketPlayClientPlayerDigging packet) {
        if (player.getConnectedClient() == null) {
            return;
        }
        PlayerBlockBreakEvent event = player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerBlockBreakEvent(player, packet.getLocation(), PlayerBlockBreakEvent.Action.values()[packet.getAction().ordinal()]));
        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerBlockChange(packet.getLocation(), player.getConnectedClient().getBlockAccess().getBlockState(packet.getLocation())));
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CHAT, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY, priority = EventPriority.FIRST)
    private void handleChat(DefaultPlayer player, PacketPlayClientChatMessage chat) throws Exception {
        int maxLength = (player.getVersion() >= ProtocolIds.Versions.MINECRAFT_1_11) ? 256 : 100;
        if (chat.getMessage().length() >= maxLength) {
            throw CancelProceedException.INSTANCE;
        }

        if (chat.getMessage().startsWith("/proxy ")) {
            try {
                CommandMap commandMap = player.getProxy().getServiceRegistry().getProviderUnchecked(CommandMap.class);
                if (commandMap.process(player, chat.getMessage().replaceFirst("/proxy ", "")) != CommandResult.NOT_FOUND) {
                    throw CancelProceedException.INSTANCE;
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                player.sendMessage("Unable to process command: " + ex.getMessage());
                throw CancelProceedException.INSTANCE;
            }

            return;
        }

        ChatEvent event = new ChatEvent(player, ProtocolDirection.TO_SERVER, ChatMessageType.CHAT, LegacyComponentSerializer.legacy().deserialize(chat.getMessage()));
        if (player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(LegacyComponentSerializer.legacy().serialize(event.getMessage()));
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CUSTOM_PAYLOAD, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY)
    private void handlePluginMessage(DefaultPlayer player, PacketPlayClientCustomPayload pluginMessage) throws Exception {
        PluginMessageEvent event = new PluginMessageEvent(player, ProtocolDirection.TO_SERVER, pluginMessage.getTag(), pluginMessage.getData());
        if (player.getProxy().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        pluginMessage.setTag(event.getTag());
        pluginMessage.setData(event.getData());
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.TAB_COMPLETE, directions = ProtocolDirection.TO_SERVER)
    public void handle(DefaultPlayer player, PacketPlayClientTabCompleteRequest request) {
        if (!request.getCursor().startsWith("/")) {
            return;
        }
        if (!request.getCursor().startsWith("/proxy")) {
            if (!request.getCursor().contains(" ")) {
                player.setLastCommandCompleteRequest(request.getCursor());
            }
            return;
        }
        List<String> suggestions = player.getProxy().getServiceRegistry().getProviderUnchecked(CommandMap.class).getSuggestions(player, request.getCursor().substring("/proxy ".length()));
        if (!suggestions.isEmpty()) {
            player.sendPacket(new PacketPlayServerTabCompleteResponse(suggestions));
            throw CancelProceedException.INSTANCE;
        }
    }

}
