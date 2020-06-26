package com.github.derrop.plugins.discord;

import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "com.github.derrop.plugins.discord",
        displayName = "DiscordBot",
        version = 1,
        website = "https://github.com/derrop",
        authors = "derrop"
)
public class DiscordBot {

    private static final String DEFAULT_TOKEN = "Your Token";

    private final Gson gson = new Gson();

    private String token;
    private JDA jda;

    @Inject(state = PluginState.LOADED)
    public void loadConfig(PluginContainer container) throws IOException {
        Files.createDirectories(container.getDataFolder());
        Path path = container.getDataFolder().resolve("config.json");
        if (!Files.exists(path)) {
            JsonObject object = new JsonObject();
            object.addProperty("token", DEFAULT_TOKEN);
            try (OutputStream outputStream = Files.newOutputStream(path)) {
                this.gson.toJson(object, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            }
            return;
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            this.token = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject().get("token").getAsString();
        }
    }

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry) throws LoginException {
        if (this.token.equals(DEFAULT_TOKEN)) {
            return;
        }

        this.jda = JDABuilder.create(GatewayIntent.DIRECT_MESSAGES)
                .setToken(this.token)
                .addEventListeners(new ListenerAdapter() {
                    @Override
                    public void onReady(@Nonnull ReadyEvent event) {
                        event.getJDA().removeEventListener(this);


                    }
                })
                .build();

    }

    @Inject(state = PluginState.DISABLED)
    public void disable() {
        if (this.jda != null) {
            this.jda.shutdownNow();
        }
    }

}
