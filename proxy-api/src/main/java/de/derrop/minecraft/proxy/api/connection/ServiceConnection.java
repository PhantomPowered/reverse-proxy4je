package de.derrop.minecraft.proxy.api.connection;

import com.mojang.authlib.UserAuthentication;
import de.derrop.minecraft.proxy.api.Proxy;
import de.derrop.minecraft.proxy.api.task.Task;
import de.derrop.minecraft.proxy.api.task.TaskFutureListener;
import de.derrop.minecraft.proxy.api.util.MCCredentials;
import de.derrop.minecraft.proxy.api.util.NetworkAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface ServiceConnection extends Connection, AutoCloseable {

    @NotNull
    Proxy getProxy();

    @Nullable
    ProxiedPlayer getPlayer();

    @NotNull
    MCCredentials getCredentials();

    @Nullable
    UserAuthentication getAuthentication();

    @Nullable
    UUID getUniqueId();

    @Nullable
    String getName();

    int getEntityId();

    @NotNull
    NetworkAddress getServerAddress();

    void chat(String message);

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

}
