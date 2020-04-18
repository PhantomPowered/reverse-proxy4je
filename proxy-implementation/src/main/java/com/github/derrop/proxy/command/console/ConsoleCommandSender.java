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

import com.github.derrop.proxy.api.chat.component.BaseComponent;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.chat.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleCommandSender implements CommandSender {

    private static final UUID CONSOLE_UUID = UUID.randomUUID();

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(ChatColor.stripColor(message));
    }

    @Override
    public void sendMessage(@NotNull BaseComponent component) {
        this.sendMessage(ComponentSerializer.toString(component));
    }

    @Override
    public void sendMessage(@NotNull BaseComponent[] component) {
        this.sendMessage(ComponentSerializer.toString(component));
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
