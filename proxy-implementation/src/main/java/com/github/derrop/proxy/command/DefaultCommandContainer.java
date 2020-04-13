package com.github.derrop.proxy.command;

import com.github.derrop.proxy.api.command.CommandCallback;
import com.github.derrop.proxy.api.command.CommandContainer;
import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class DefaultCommandContainer implements CommandContainer {

    DefaultCommandContainer(Plugin plugin, CommandCallback commandCallback, String mainAlias, Set<String> aliases) {
        this.plugin = plugin;
        this.commandCallback = commandCallback;
        this.mainAlias = mainAlias;
        this.aliases = aliases;
    }

    private final Plugin plugin;

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
    public @Nullable Plugin getPlugin() {
        return this.plugin;
    }
}
