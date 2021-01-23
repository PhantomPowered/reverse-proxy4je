package com.github.phantompowered.proxy.connection.handler;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.exception.PermissionDeniedException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceInventory;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.event.Cancelable;
import com.github.phantompowered.proxy.api.event.Event;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.event.EventPriority;
import com.github.phantompowered.proxy.api.events.connection.ChatEvent;
import com.github.phantompowered.proxy.api.events.connection.PluginMessageEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerBlockBreakEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerBlockPlaceEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerInteractEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerInventoryClickEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerInventoryCloseEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerMoveEvent;
import com.github.phantompowered.proxy.api.events.connection.player.interact.PlayerAttackEntityEvent;
import com.github.phantompowered.proxy.api.events.connection.player.interact.PlayerInteractAtEntityEvent;
import com.github.phantompowered.proxy.api.events.connection.player.interact.PlayerInteractEntityEvent;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.api.player.GameMode;
import com.github.phantompowered.proxy.api.player.inventory.ClickType;
import com.github.phantompowered.proxy.connection.BasicServiceConnection;
import com.github.phantompowered.proxy.connection.DefaultServiceInventory;
import com.github.phantompowered.proxy.connection.player.DefaultPlayer;
import com.github.phantompowered.proxy.item.ProxyItemStack;
import com.github.phantompowered.proxy.network.wrapper.DecodedPacket;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientArmAnimation;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientBlockPlace;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientChatMessage;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientCustomPayload;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientPlayerDigging;
import com.github.phantompowered.proxy.protocol.play.client.PacketPlayClientTabCompleteRequest;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientEntityAction;
import com.github.phantompowered.proxy.protocol.play.client.entity.PacketPlayClientUseEntity;
import com.github.phantompowered.proxy.protocol.play.client.inventory.PacketPlayClientClickWindow;
import com.github.phantompowered.proxy.protocol.play.client.inventory.PacketPlayClientCloseWindow;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientLook;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPlayerPosition;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPosition;
import com.github.phantompowered.proxy.protocol.play.client.position.PacketPlayClientPositionLook;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerTabCompleteResponse;
import com.github.phantompowered.proxy.protocol.play.server.inventory.PacketPlayServerSetSlot;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerBlockChange;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ClientPacketHandler {

    @PacketHandler(protocolState = ProtocolState.PLAY, directions = ProtocolDirection.TO_SERVER, priority = EventPriority.LAST)
    public void handleGeneral(DefaultPlayer player, DecodedPacket packet) {
        if (player.getConnectedClient() != null && player.getConnectedClient().isConnected()) {

            if (packet.getPacket() != null && player.getConnectedClient() instanceof BasicServiceConnection) {
                if (player.getEntityId() != player.getConnectedClient().getEntityId()) {
                    ((BasicServiceConnection) player.getConnectedClient()).getEntityRewrite().updatePacketToServer(packet.getPacket(), player.getEntityId(), player.getConnectedClient().getEntityId());
                }
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

            default:
                break;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_DIG, directions = ProtocolDirection.TO_SERVER)
    public void handleLeftClick(DefaultPlayer player, PacketPlayClientPlayerDigging packet) {
        if (packet.getAction() != PacketPlayClientPlayerDigging.Action.START_DESTROY_BLOCK
                && packet.getAction() != PacketPlayClientPlayerDigging.Action.ABORT_DESTROY_BLOCK
                && packet.getAction() != PacketPlayClientPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            return;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK);
        player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (event.isCancelled()) {
            throw CancelProceedException.INSTANCE;
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
            case ATTACK:
                event = new PlayerAttackEntityEvent(player, entity);
                break;
            case INTERACT:
                event = new PlayerInteractEntityEvent(player, entity);
                break;
            case INTERACT_AT:
                event = new PlayerInteractAtEntityEvent(player, entity, packet.getHitVector());
                break;
            default:
                throw new IllegalStateException("Received unknown action " + packet.getAction());
        }

        player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (((Cancelable) event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

    private boolean isTargetingBlock(ServiceConnection connection) {
        int distance = connection.getWorldDataProvider().getOwnGameMode() == GameMode.CREATIVE ? APIUtil.CREATIVE_PLACE_DISTANCE : APIUtil.SURVIVAL_PLACE_DISTANCE;
        try {
            Location targetedBlock = connection.getTargetBlock(distance);
            if (targetedBlock != null) {
                return true;
            }
        } catch (IllegalStateException ignored) {
        }

        return false;
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.ARM_ANIMATION, directions = ProtocolDirection.TO_SERVER)
    public void handleArmAnimation(DefaultPlayer player, PacketPlayClientArmAnimation packet) {
        if (player.getConnectedClient() == null || this.isTargetingBlock(player.getConnectedClient())) {
            // Left click block is sent in the Digging packet
            return;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_AIR);
        player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
        if (event.isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_PLACE, directions = ProtocolDirection.TO_SERVER)
    public void handleBlockPlace(DefaultPlayer player, PacketPlayClientBlockPlace packet) {
        if (player.getConnectedClient() == null) {
            return;
        }
        if (packet.getPlacedBlockDirection() == 255) {
            boolean block = this.isTargetingBlock(player.getConnectedClient());
            PlayerInteractEvent event = new PlayerInteractEvent(player, block ? PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK : PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
            player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
            if (event.isCancelled()) {
                throw CancelProceedException.INSTANCE;
            }
            return;
        }

        Facing facing = Facing.getByIndex(packet.getPlacedBlockDirection());
        Location blockLocation = packet.getLocation().offset(facing);
        PlayerBlockPlaceEvent event = player.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerBlockPlaceEvent(player, packet.getLocation(), blockLocation, packet.getStack(), facing, new Vector(packet.getFacingX(), packet.getFacingY(), packet.getFacingZ())));

        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerBlockChange(blockLocation, player.getConnectedClient().getBlockAccess().getBlockState(blockLocation)));
            if (player.getConnectedClient() != null) {
                ServiceInventory inventory = player.getConnectedClient().getInventory();
                int slot = inventory.getHeldItemSlot();
                ItemStack item = inventory.getHotBarItem(slot);
                if (item != null) {
                    player.sendPacket(new PacketPlayServerSetSlot((byte) 0, slot + DefaultServiceInventory.HOTBAR_OFFSET, item));
                }
            }
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.WINDOW_CLICK, directions = ProtocolDirection.TO_SERVER)
    public void handleWindowClick(DefaultPlayer player, PacketPlayClientClickWindow packet) {
        ClickType click = packet.getClick();
        if (click == null) {
            return;
        }

        PlayerInventoryClickEvent event = player.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerInventoryClickEvent(player, packet.getSlot(), click));
        if (event.isCancelled()) {
            // TODO this works when clicking with the cursor, but not with the hotkeys
            player.sendPacket(new PacketPlayServerSetSlot((byte) -1, -1, ProxyItemStack.AIR)); // window -1, slot -1 = item on cursor
            if (player.getConnectedClient() != null) {
                player.sendPacket(new PacketPlayServerSetSlot((byte) 0, packet.getSlot(), player.getConnectedClient().getInventory().getItem(packet.getSlot())));
            }
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CLOSE_WINDOW, directions = ProtocolDirection.TO_SERVER)
    public void handleWindowClose(DefaultPlayer player, PacketPlayClientCloseWindow packet) {
        PlayerInventoryCloseEvent event = player.getServiceRegistry().getProviderUnchecked(EventManager.class)
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

        PlayerMoveEvent event = player.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerMoveEvent(player, connection.getLocation(), newLocation));
        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerPosition(event.getFrom()));
            throw CancelProceedException.INSTANCE;
        }

        connection.updateLocation(newLocation);
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.BLOCK_DIG, directions = ProtocolDirection.TO_SERVER)
    public void handleBlockDig(DefaultPlayer player, PacketPlayClientPlayerDigging packet) {
        if (player.getConnectedClient() == null) {
            return;
        }
        PlayerBlockBreakEvent event = player.getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new PlayerBlockBreakEvent(player, packet.getLocation(), PlayerBlockBreakEvent.Action.values()[packet.getAction().ordinal()]));
        if (event.isCancelled()) {
            player.sendPacket(new PacketPlayServerBlockChange(packet.getLocation(), player.getConnectedClient().getBlockAccess().getBlockState(packet.getLocation())));
            throw CancelProceedException.INSTANCE;
        }
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CHAT, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY, priority = EventPriority.FIRST)
    private void handleChat(DefaultPlayer player, PacketPlayClientChatMessage chat) {
        int maxLength = (player.getVersion() >= ProtocolIds.Versions.MINECRAFT_1_11) ? 256 : 100;
        if (chat.getMessage().length() >= maxLength) {
            throw CancelProceedException.INSTANCE;
        }

        if (chat.getMessage().toLowerCase().startsWith(CommandMap.INGAME_PREFIX)) {
            try {
                CommandMap commandMap = player.getServiceRegistry().getProviderUnchecked(CommandMap.class);
                if (commandMap.process(player, chat.getMessage().substring(CommandMap.INGAME_PREFIX.length())) != CommandResult.NOT_FOUND) {
                    throw CancelProceedException.INSTANCE;
                }
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                player.sendMessage("Unable to process command: " + ex.getMessage());
                throw CancelProceedException.INSTANCE;
            }

            return;
        }

        ChatEvent event = new ChatEvent(player, ProtocolDirection.TO_SERVER, ChatMessageType.CHAT, LegacyComponentSerializer.legacySection().deserialize(chat.getMessage()));
        if (player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
            throw CancelProceedException.INSTANCE;
        }

        chat.setMessage(LegacyComponentSerializer.legacySection().serialize(event.getMessage()));
    }

    @PacketHandler(packetIds = ProtocolIds.FromClient.Play.CUSTOM_PAYLOAD, directions = ProtocolDirection.TO_SERVER, protocolState = ProtocolState.PLAY)
    private void handlePluginMessage(DefaultPlayer player, PacketPlayClientCustomPayload pluginMessage) {
        if (pluginMessage.getTag().equalsIgnoreCase("MC|Brand")) {
            throw CancelProceedException.INSTANCE;
        }

        PluginMessageEvent event = new PluginMessageEvent(player, ProtocolDirection.TO_SERVER, pluginMessage.getTag(), pluginMessage.getData());
        if (player.getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event).isCancelled()) {
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

        if (!request.getCursor().toLowerCase().startsWith(CommandMap.INGAME_PREFIX)) {
            if (!request.getCursor().contains(" ")) {
                player.setLastCommandCompleteRequest(request.getCursor());
            }

            return;
        }

        List<String> suggestions = player.getServiceRegistry().getProviderUnchecked(CommandMap.class).getSuggestions(player, request.getCursor().substring(CommandMap.INGAME_PREFIX.length()));
        if (!suggestions.isEmpty()) {
            player.sendPacket(new PacketPlayServerTabCompleteResponse(suggestions));
            throw CancelProceedException.INSTANCE;
        }
    }
}
