package com.github.derrop.proxy.serialization;

import com.github.derrop.proxy.api.chat.component.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.chat.*;

public final class ComponentSerializer {

    private static final Gson GSON = new GsonBuilder().
    registerTypeAdapter(BaseComponent .class, new net.md_5.bungee.chat.ComponentSerializer()).
    registerTypeAdapter(TextComponent .class, new TextComponentSerializer()).
    registerTypeAdapter(TranslatableComponent .class, new TranslatableComponentSerializer()).
    registerTypeAdapter(KeybindComponent .class, new KeybindComponentSerializer()).
    registerTypeAdapter(ScoreComponent .class, new ScoreComponentSerializer()).
    registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer()).
    create();

}
