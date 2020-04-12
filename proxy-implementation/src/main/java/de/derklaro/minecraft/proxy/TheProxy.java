package de.derklaro.minecraft.proxy;

import de.derklaro.minecraft.proxy.brand.ProxyBrandChangeListener;
import de.derrop.minecraft.proxy.api.event.EventManager;
import de.derklaro.minecraft.proxy.event.basic.DefaultEventManager;
import de.derklaro.minecraft.proxy.labymod.LabyModListener;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TheProxy {

    public static final Path ACCOUNT_PATH = Paths.get("accounts.txt");

    public static TheProxy instance;

    private final EventManager eventManager = new DefaultEventManager();

    public TheProxy() {
        instance = this;
    }

    public void handleStart() {
        this.eventManager.registerListener(new LabyModListener());
        this.eventManager.registerListener(new ProxyBrandChangeListener());
    }

    public void end() {
        this.eventManager.unregisterAll();
    }

    @NotNull
    public EventManager getEventManager() {
        return eventManager;
    }

    public static TheProxy getTheProxy() {
        return instance;
    }
}
