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
package com.github.derrop.proxy.command;

import com.github.derrop.proxy.api.command.CommandCallback;
import com.github.derrop.proxy.api.command.CommandContainer;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class DefaultCommandContainer implements CommandContainer {

    DefaultCommandContainer(PluginContainer pluginContainer, CommandCallback commandCallback, String mainAlias, Set<String> aliases) {
        this.pluginContainer = pluginContainer;
        this.commandCallback = commandCallback;
        this.mainAlias = mainAlias;
        this.aliases = aliases;
    }

    private final PluginContainer pluginContainer;

    private final CommandCallback commandCallback;

    private final String mainAlias;

    private final Set<String> aliases;

    @Override
    public @NotNull String getMainAlias() {
        return this.mainAlias;
    }

    @Override
    public @NotNull Set<String> getOtherAliases() {
        return this.aliases;
    }

    @Override
    public @NotNull CommandCallback getCallback() {
        return this.commandCallback;
    }

    @Override
    public @Nullable PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }
}
