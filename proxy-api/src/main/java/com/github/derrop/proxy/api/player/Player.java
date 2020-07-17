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
package com.github.derrop.proxy.api.player;

import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.block.Material;
import com.github.derrop.proxy.api.block.half.HorizontalHalf;
import com.github.derrop.proxy.api.chat.ChatMessageType;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.Connection;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.entity.EntityStatusType;
import com.github.derrop.proxy.api.entity.types.Entity;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.player.inventory.PlayerInventory;
import com.github.derrop.proxy.api.util.ProvidedTitle;
import net.kyori.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface Player extends OfflinePlayer, Connection, CommandSender, Entity {

    Proxy getProxy();

    void sendMessage(ChatMessageType position, Component... messages);

    void sendMessage(ChatMessageType position, Component message);

    void sendActionBar(int units, Component... message);

    void appendActionBar(@NotNull HorizontalHalf side, @NotNull Supplier<String> message);

    void sendData(String channel, byte[] data);

    void useClientSafe(ServiceConnection connection);

    void useClient(ServiceConnection connection);

    @Nullable
    ServiceConnection getConnectedClient();

    int getVersion();

    void disableAutoReconnect();

    void enableAutoReconnect();

    void sendServerMessage(String message);

    // todo: proper

    void setTabHeader(Component header, Component footer);

    void resetTabHeader();

    // end

    void sendTitle(ProvidedTitle providedTitle);

    void sendBlockChange(Location pos, int blockState);

    void sendBlockChange(Location pos, Material material);

    PlayerInventory getInventory();

    void sendEntityStatus(@Nullable Entity entity, @NotNull EntityStatusType statusType);

    default void sendSelfEntityStatus(EntityStatusType statusType) {
        this.sendEntityStatus(null, statusType);
    }

}
