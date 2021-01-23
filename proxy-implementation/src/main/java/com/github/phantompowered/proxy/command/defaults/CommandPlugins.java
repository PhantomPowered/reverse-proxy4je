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
package com.github.phantompowered.proxy.command.defaults;

import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginManager;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandPlugins extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandPlugins(ServiceRegistry registry) {
        super("proxy.command.plugins", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        PluginManager pluginManager = this.registry.getProviderUnchecked(PluginManager.class);
        Predicate<PluginContainer> filter = null;
        if (args.length != 0) {
            String match = args[0].toLowerCase();
            filter = plugin -> (plugin.getDisplayName() != null && plugin.getDisplayName().toLowerCase().contains(match)) || plugin.getId().toLowerCase().contains(match);
        }

        String plugins = pluginManager.getPlugins().stream()
                .filter(filter != null ? filter : plugin -> true)
                .map(plugin -> (plugin.getDisplayName() == null ? plugin.getId() : plugin.getDisplayName()) + " v" + plugin.getVersion())
                .collect(Collectors.joining("\n"));

        if (args.length == 0) {
            String message = "Enabled plugins: " + (plugins.isEmpty() ? "none" : "\n" + plugins);
            for (String s : message.split("\n")) {
                sender.sendMessage(s);
            }
            return CommandResult.SUCCESS;
        }

        if (plugins.isEmpty()) {
            sender.sendMessage("No plugins matching the given word found");
            return CommandResult.BREAK;
        }
        String message = "Plugins matching the given text found: \n" + plugins;
        for (String s : message.split("\n")) {
            sender.sendMessage(s);
        }
        return CommandResult.SUCCESS;
    }
}
