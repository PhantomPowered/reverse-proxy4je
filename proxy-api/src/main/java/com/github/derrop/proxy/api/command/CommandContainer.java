package com.github.derrop.proxy.api.command;

import com.github.derrop.proxy.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface CommandContainer {

    @NotNull
    String getMainAlias();

    @NotNull
    Set<String> getOtherAliases();

    @NotNull
    CommandCallback getCallback();

    @Nullable Plugin getPlugin();
}
