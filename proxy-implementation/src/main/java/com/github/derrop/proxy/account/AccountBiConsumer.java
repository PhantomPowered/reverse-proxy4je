package com.github.derrop.proxy.account;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.task.Task;
import com.github.derrop.proxy.api.task.TaskFutureListener;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.basic.BasicServiceConnection;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class AccountBiConsumer implements BiConsumer<MCCredentials, NetworkAddress> {

    @Override
    public void accept(MCCredentials mcCredentials, NetworkAddress networkAddress) {
        try {
            ServiceConnection connection = new BasicServiceConnection(MCProxy.getInstance(), mcCredentials, networkAddress);

            connection.connect(new TaskFutureListener<Boolean>() {
                @Override
                public void onCancel(@NotNull Task<Boolean> task) {
                    System.err.println("Connection to " + connection.getServerAddress() + " cancelled");
                }

                @Override
                public void onFailure(@NotNull Task<Boolean> task) {
                    Throwable lastException = task.getException();
                    if (lastException == null) {
                        System.err.println("Got kicked from " + connection.getServerAddress() + " as " + connection.getCredentials());
                        return;
                    }

                    System.err.println("Got kicked from " + connection.getServerAddress()
                            + " as " + connection.getCredentials() + ": " );//+ lastException.getMessage().replace('\n', ' '));
                }

                @Override
                public void onSuccess(@NotNull Task<Boolean> task) {
                    Boolean result = task.getResult();
                    if (result != null && result) {
                        System.out.println("Successfully opended connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
                        return;
                    }

                    System.err.println("Unable to open connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
                }
            });
        } catch (final AuthenticationException exception) {
            exception.printStackTrace();
        }
    }
}
