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
package com.github.phantompowered.proxy.api.connection;

import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.task.Task;
import com.github.phantompowered.proxy.api.task.TaskFutureListener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DefaultConnectionHandler implements TaskFutureListener<ServiceConnectResult> {

    private final ServiceConnection connection;
    private final Consumer<String> infoHandler;
    private final Consumer<String> errorHandler;

    public DefaultConnectionHandler(ServiceConnection connection, Consumer<String> infoHandler, Consumer<String> errorHandler) {
        this.connection = connection;
        this.infoHandler = infoHandler;
        this.errorHandler = errorHandler;
    }

    public static DefaultConnectionHandler console(ServiceConnection connection) {
        return new DefaultConnectionHandler(connection, System.out::println, System.err::println);
    }

    public static DefaultConnectionHandler coloredCommand(ServiceConnection connection, CommandSender sender) {
        return new DefaultConnectionHandler(connection, s -> sender.sendMessage("§a" + s), s -> sender.sendMessage("§c" + s));
    }

    public static DefaultConnectionHandler plainCommand(ServiceConnection connection, CommandSender sender) {
        return new DefaultConnectionHandler(connection, sender::sendMessage, sender::sendMessage);
    }

    @Override
    public void onCancel(@NotNull Task<ServiceConnectResult> task) {
        this.errorHandler.accept("Connection to " + connection.getServerAddress() + " cancelled");
    }

    @Override
    public void onFailure(@NotNull Task<ServiceConnectResult> task) {
        String base = "Got kicked from " + connection.getServerAddress() + " as " + connection.getCredentials();
        Throwable lastException = task.getException();

        if (lastException == null || lastException.getMessage() == null) {
            this.errorHandler.accept(base);
            return;
        }

        this.errorHandler.accept(base + ": " + lastException.getMessage().replace('\n', ' '));
    }

    @Override
    public void onSuccess(@NotNull Task<ServiceConnectResult> task) {
        ServiceConnectResult result = task.getResult();
        if (result == null) {
            throw new IllegalStateException("Null result");
        }
        if (result.isSuccess()) {
            this.infoHandler.accept("Successfully opened connection to " + connection.getServerAddress() + " as " + connection.getCredentials());
            return;
        }

        String message = "Unable to open connection to " + connection.getServerAddress() + " as " + connection.getCredentials();
        if (result.getFailureReason() != null) {
            message += "; Reason: " + LegacyComponentSerializer.legacySection().serialize(result.getFailureReason());
        }
        this.errorHandler.accept(message);
    }

}
