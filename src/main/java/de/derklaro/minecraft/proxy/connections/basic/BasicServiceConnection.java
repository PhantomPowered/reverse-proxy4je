package de.derklaro.minecraft.proxy.connections.basic;

import com.google.common.base.Preconditions;
import com.mojang.authlib.exceptions.AuthenticationException;
import de.derklaro.minecraft.proxy.connections.ServiceConnection;
import de.derklaro.minecraft.proxy.task.Task;
import de.derklaro.minecraft.proxy.task.TaskFutureListener;
import de.derklaro.minecraft.proxy.task.basic.DefaultTask;
import de.derklaro.minecraft.proxy.task.util.EmptyTaskFutureListener;
import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.ban.BanTester;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.exception.KickedException;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BasicServiceConnection implements ServiceConnection {

    private static final Set<NetworkAddress> BANNED_ADDRESSES = new HashSet<>();

    public BasicServiceConnection(MCCredentials credentials, NetworkAddress networkAddress) {
        this(credentials, networkAddress, true);
    }

    public BasicServiceConnection(MCCredentials credentials, NetworkAddress networkAddress, boolean reScheduleOnFailure) {
        this.credentials = credentials;
        this.networkAddress = networkAddress;
        this.reScheduleOnFailure = reScheduleOnFailure;
    }

    private final MCCredentials credentials;

    private final NetworkAddress networkAddress;

    private ConnectedProxyClient client;

    private boolean reScheduleOnFailure;

    @Override
    public @NotNull MCCredentials getCredentials() {
        return this.credentials;
    }

    @Override
    public @NotNull NetworkAddress getNetworkAddress() {
        return this.networkAddress;
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
            System.err.println("To prevent ip bans the connection to " + this.networkAddress + " was baorted");
            return Task.completedTask(false, listener);
        }

        Task<Boolean> task = new DefaultTask<>();
        for (TaskFutureListener<Boolean> booleanTaskFutureListener : listener) {
            task.addListener(booleanTaskFutureListener);
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            this.client = new ConnectedProxyClient();

            try {
                if (!client.performMojangLogin(this.credentials)) {
                    task.complete(false);
                    this.client = null;
                    return;
                }

                Boolean result = this.client.connect(this.networkAddress, null).get(5, TimeUnit.SECONDS);
                task.complete(result != null && result);
            } catch (final AuthenticationException ex) {
                task.completeExceptionally(ex);
            } catch (final InterruptedException | ExecutionException | TimeoutException exception) {
                task.completeExceptionally(exception);
                this.client = null;

                if (exception.getCause() instanceof KickedException) {
                    if (MCProxy.getInstance().getBanTester().isBanned(exception.getMessage()) == BanTester.BanTestResult.BANNED) {
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
            return Task.completedTask(false, listener);
        }

        this.close();
        return this.connect(listener);
    }

    @Override
    public @Nullable ConnectedProxyClient getClient() {
        return this.client;
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
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
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
}
