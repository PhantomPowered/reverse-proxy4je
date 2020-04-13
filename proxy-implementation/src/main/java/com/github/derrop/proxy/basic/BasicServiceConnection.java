package com.github.derrop.proxy.basic;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.connection.ProxiedPlayer;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.packet.Packet;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.api.util.ChatMessageType;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.ban.BanTester;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.exception.KickedException;
import com.github.derrop.proxy.minecraft.SessionUtils;
import com.github.derrop.proxy.task.DefaultTask;
import com.github.derrop.proxy.task.EmptyTaskFutureListener;
import com.github.derrop.proxy.task.util.TaskUtil;
import com.google.common.base.Preconditions;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.md_5.bungee.ChatComponentTransformer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.Chat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BasicServiceConnection implements ServiceConnection {

    private static final Set<NetworkAddress> BANNED_ADDRESSES = new HashSet<>();

    public BasicServiceConnection(MCProxy proxy, MCCredentials credentials, NetworkAddress networkAddress) throws AuthenticationException {
        this(proxy, credentials, networkAddress, true);
    }

    public BasicServiceConnection(MCProxy proxy, MCCredentials credentials, NetworkAddress networkAddress, boolean reScheduleOnFailure) throws AuthenticationException {
        this.proxy = proxy;
        this.credentials = credentials;
        this.networkAddress = networkAddress;
        this.reScheduleOnFailure = reScheduleOnFailure;

        if (credentials.isOffline()) {
            this.authentication = null;
            return;
        }

        System.out.println("Logging in " + credentials.getEmail() + "...");
        this.authentication = SessionUtils.logIn(credentials.getEmail(), credentials.getPassword());
        System.out.println("Successfully logged in with " + credentials.getEmail() + "!");
    }

    private final MCProxy proxy;

    private final MCCredentials credentials;
    private final UserAuthentication authentication;

    private final NetworkAddress networkAddress;

    private ConnectedProxyClient client;

    private boolean reScheduleOnFailure;

    @Override
    public @NotNull Proxy getProxy() {
        return this.proxy;
    }

    @Override
    public @Nullable ProxiedPlayer getPlayer() {
        return this.client.getRedirector();
    }

    @Override
    public @NotNull MCCredentials getCredentials() {
        return this.credentials;
    }

    @Override
    public @Nullable UserAuthentication getAuthentication() {
        return this.client.getAuthentication();
    }

    @Override
    public UUID getUniqueId() {
        return this.credentials.isOffline() ? UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.credentials.getUsername()).getBytes()) : this.authentication.getSelectedProfile().getId();
    }

    @Override
    public String getName() {
        return this.credentials.isOffline() ? this.credentials.getUsername() : this.authentication.getSelectedProfile().getName();
    }

    @Override
    public int getEntityId() {
        return this.client.getEntityId();
    }

    public ConnectedProxyClient getClient() {
        return this.client;
    }

    public void sendPacket(Object packet) {
        this.client.getChannelWrapper().write(packet);
    }

    @Override
    public @NotNull NetworkAddress getServerAddress() {
        return this.networkAddress;
    }

    @Override
    public void chat(@NotNull String message) {
        this.client.getChannelWrapper().write(new Chat(message));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull String message) {
        this.client.getChannelWrapper().write(new Chat(message, (byte) type.ordinal()));
    }

    @Override
    public void chat(@NotNull BaseComponent component) {
        component = ChatComponentTransformer.getInstance().transform(this.client.getRedirector(), component)[0];
        this.chat(ComponentSerializer.toString(component));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull BaseComponent component) {
        component = ChatComponentTransformer.getInstance().transform(this.client.getRedirector(), component)[0];
        if (type == ChatMessageType.ACTION_BAR) {
            this.displayMessage(type, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(component))));
        } else {
            this.displayMessage(type, ComponentSerializer.toString(component));
        }
    }

    @Override
    public void chat(@NotNull BaseComponent... components) {
        components = ChatComponentTransformer.getInstance().transform(this.client.getRedirector(), components);
        this.chat(ComponentSerializer.toString(components));
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull BaseComponent... components) {
        components = ChatComponentTransformer.getInstance().transform(this.client.getRedirector(), components);
        if (type == ChatMessageType.ACTION_BAR) {
            this.displayMessage(type, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(components))));
        } else {
            this.displayMessage(type, ComponentSerializer.toString(components));
        }
    }

    @Override
    public @NotNull Task<Boolean> connect() {
        return this.connect(EmptyTaskFutureListener.BOOL_INSTANCE);
    }

    @Override
    public @NotNull Task<Boolean> connect(@NotNull TaskFutureListener<Boolean> listener) {
        return this.connect(Collections.singletonList(listener));
    }

    @Override
    public @NotNull Task<Boolean> connect(@NotNull Collection<TaskFutureListener<Boolean>> listener) {
        if (BANNED_ADDRESSES.contains(this.networkAddress)) {
            System.err.println("To prevent ip bans the connection to " + this.networkAddress + " was aborted");
            return TaskUtil.completedTask(false, listener);
        }

        Task<Boolean> task = new DefaultTask<>();
        for (TaskFutureListener<Boolean> booleanTaskFutureListener : listener) {
            task.addListener(booleanTaskFutureListener);
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            this.client = new ConnectedProxyClient(this.proxy, this);

            try {
                if (!client.performMojangLogin(this.credentials)) {
                    task.complete(false);
                    this.client = null;
                    return;
                }

                Boolean result = this.client.connect(this.networkAddress, null).get(5, TimeUnit.SECONDS);
                if (result != null && result) {
                    this.proxy.addOnlineClient(this);
                    task.complete(true);
                } else {
                    task.complete(false);
                }
            } catch (final AuthenticationException ex) {
                task.completeExceptionally(ex);
            } catch (final InterruptedException | ExecutionException | TimeoutException exception) {
                task.completeExceptionally(exception);
                this.client = null;

                if (exception.getCause() instanceof KickedException) {
                    if (BanTester.isBanned(exception.getMessage()) == BanTester.BanTestResult.BANNED) {
                        BANNED_ADDRESSES.add(this.networkAddress);
                        MCProxy.getInstance().getLogger().warn("Preventing connections to " + networkAddress + " because " + credentials.getEmail() + " is banned");
                        return;
                    }
                }

                if (this.isReScheduleOnFailure()) {
                    this.reSchedule(listener);
                }
            }
        });
        return task;
    }

    @Override
    public @NotNull Task<Boolean> reconnect() {
        return this.reconnect(EmptyTaskFutureListener.BOOL_INSTANCE);
    }

    @Override
    public @NotNull Task<Boolean> reconnect(@NotNull TaskFutureListener<Boolean> listener) {
        return this.reconnect(Collections.singletonList(listener));
    }

    @Override
    public @NotNull Task<Boolean> reconnect(@NotNull Collection<TaskFutureListener<Boolean>> listener) {
        if (this.client == null) {
            return TaskUtil.completedTask(false, listener);
        }

        this.close();
        return this.connect(listener);
    }

    @Override
    public void setReScheduleOnFailure(boolean reScheduleOnFailure) {
        this.reScheduleOnFailure = reScheduleOnFailure;
    }

    @Override
    public boolean isReScheduleOnFailure() {
        return this.reScheduleOnFailure;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return this.client.getChannelWrapper().getHandle().remoteAddress();
    }

    @Override
    public void disconnect(String reason) {
        this.close();
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        this.close();
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.close();
    }

    @Override
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    @Override
    public void unregister() {
        this.proxy.unregisterConnection(this);
    }

    private void reSchedule(Collection<TaskFutureListener<Boolean>> listener) {
        Preconditions.checkArgument(this.client == null);

        try {
            Thread.sleep(20000);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }

        this.connect(listener);
    }

    @Override
    public void close() {
        if (!this.isConnected()) {
            return;
        }

        this.client.disconnect();
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        if (this.client == null || this.client.getRedirector() == null) {
            return;
        }

        this.client.getRedirector().sendPacket(packet);
    }
}
