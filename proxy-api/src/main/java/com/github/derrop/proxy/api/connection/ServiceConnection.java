package com.github.derrop.proxy.api.connection;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.scoreboard.Scoreboard;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.mojang.authlib.UserAuthentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface ServiceConnection extends Connection, AutoCloseable {

    @NotNull
    Proxy getProxy();

    @Nullable
    Player getPlayer();

    @NotNull
    MCCredentials getCredentials();

    @Nullable
    UserAuthentication getAuthentication();

    @Nullable
    UUID getUniqueId();

    @Nullable
    String getName();

    int getEntityId();

    boolean isOnGround();

    ServiceWorldDataProvider getWorldDataProvider();

    @NotNull
    NetworkAddress getServerAddress();

    void chat(@NotNull String message);

    void displayMessage(@NotNull ChatMessageType type, @NotNull String message);

    void chat(@NotNull BaseComponent component);

    void displayMessage(@NotNull ChatMessageType type, @NotNull BaseComponent component);

    void chat(@NotNull BaseComponent... components);

    void displayMessage(@NotNull ChatMessageType type, @NotNull BaseComponent... components);

    @NotNull
    Task<Boolean> connect();

    @NotNull
    Task<Boolean> connect(@NotNull TaskFutureListener<Boolean> listener);

    @NotNull
    Task<Boolean> connect(@NotNull Collection<TaskFutureListener<Boolean>> listener);

    @NotNull
    Task<Boolean> reconnect();

    @NotNull
    Task<Boolean> reconnect(@NotNull TaskFutureListener<Boolean> listener);

    @NotNull
    Task<Boolean> reconnect(@NotNull Collection<TaskFutureListener<Boolean>> listener);

    void setReScheduleOnFailure(boolean reScheduleOnFailure);

    boolean isReScheduleOnFailure();

    boolean isConnected();

    void unregister();

    Scoreboard getScoreboard();

    BlockAccess getBlockAccess();

}
