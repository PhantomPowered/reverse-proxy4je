package de.derklaro.minecraft.proxy.connections;

import de.derklaro.minecraft.proxy.task.Task;
import de.derklaro.minecraft.proxy.task.TaskFutureListener;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ServiceConnection extends AutoCloseable {

    @NotNull
    MCCredentials getCredentials();

    @NotNull
    NetworkAddress getNetworkAddress();

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

    @Nullable ConnectedProxyClient getClient();

    void setReScheduleOnFailure(boolean reScheduleOnFailure);

    boolean isReScheduleOnFailure();

    boolean isConnected();
}
