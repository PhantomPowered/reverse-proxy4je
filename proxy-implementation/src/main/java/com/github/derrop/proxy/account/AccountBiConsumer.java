/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
            connection.setReScheduleOnFailure(true);

            connection.connect(new TaskFutureListener<Boolean>() {
                @Override
                public void onCancel(@NotNull Task<Boolean> task) {
                    System.err.println("Connection to " + connection.getServerAddress() + " cancelled");
                }

                @Override
                public void onFailure(@NotNull Task<Boolean> task) {
                    Throwable lastException = task.getException();
                    if (lastException == null || lastException.getMessage() == null) {
                        System.err.println("Got kicked from " + connection.getServerAddress() + " as " + connection.getCredentials());
                        return;
                    }

                    System.err.println("Got kicked from " + connection.getServerAddress()
                            + " as " + connection.getCredentials() + ": " + lastException.getMessage().replace('\n', ' '));
                }

                @Override
                public void onSuccess(@NotNull Task<Boolean> task) {
                    Boolean result = task.getResult();
                    if (result != null && result) {
                        System.out.println("Successfully opened connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
                        return;
                    }

                    System.err.println("Unable to open connection to " + connection.getServerAddress() + " as " + connection.getCredentials() + (task.getException() != null ? ": " + task.getException().getMessage() : ""));
                }
            });
            Thread.sleep(500);
        } catch (final AuthenticationException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
