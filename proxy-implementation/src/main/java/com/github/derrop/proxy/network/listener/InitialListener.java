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
package com.github.derrop.proxy.network.listener;

import com.github.derrop.proxy.api.chat.ChatColor;
import com.github.derrop.proxy.api.network.channel.NetworkChannel;
import com.github.derrop.proxy.network.channel.ChannelListener;
import com.github.derrop.proxy.util.Utils;
import org.jetbrains.annotations.NotNull;

public class InitialListener implements ChannelListener {

    @Override
    public void handleChannelActive(@NotNull NetworkChannel channel) {
        channel.setProperty(InitialHandler.INIT_STATE, InitialHandler.State.HANDSHAKE);
    }

    @Override
    public void handleException(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
        if (InitialHandler.canSendKickMessage(channel)) {
            InitialHandler.disconnect(channel, ChatColor.RED + Utils.stringifyException(cause));
        } else {
            channel.close();
        }
    }

}
