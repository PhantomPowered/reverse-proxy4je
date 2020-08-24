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
package com.github.derrop.proxy.command.console;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.logging.ProxyLogLevels;
import com.github.derrop.proxy.logging.ProxyLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleCommandSender implements CommandSender {

    private static final UUID CONSOLE_UUID = UUID.randomUUID();

    public ConsoleCommandSender(ServiceRegistry registry) {
        this.registry = registry;
    }

    private final ServiceRegistry registry;

    @Override
    public void sendMessage(@NotNull String message) {
        ProxyLogger logger = this.registry.getProviderUnchecked(ProxyLogger.class);
        logger.logp(ProxyLogLevels.COMMAND, "", "", ChatColor.stripColor(message));
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        this.sendMessage(LegacyComponentSerializer.legacySection().serialize(component));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "Console";
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return CONSOLE_UUID;
    }
}
